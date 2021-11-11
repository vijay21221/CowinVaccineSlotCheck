package com.example

import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpMethod
import io.micronaut.scheduling.annotation.Scheduled
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import javax.inject.Inject
import javax.inject.Singleton
import java.text.SimpleDateFormat

@Singleton
@CompileStatic
class CoWinService {
    private static final Log log = LogFactory.getLog(CoWinService.class)
    private static final String COWIN_BASE_URL = "https://cdn-api.co-vin.in/api"
    private List<Map> states = []

    @Inject
    TelegramService telegramService

    @Property(name = "cowin.state")
    String state

    @Property(name = "cowin.district")
    String district

    @Property(name = "cowin.nearBy.places")
    List<String> nearByPlaces


    @Scheduled(initialDelay = "10s")
    void onStartUp(){
        log.info("Fetching states from CoWin......")
        getStates()
        if(states.size() > 0){
            log.info("Successfully fetched states from CoWin!!")
            log.info("States : ${states.join(' ,')}")
        }
    }

    void getStates(){
        ApiRequestInfo apiRequestInfo = new ApiRequestInfo(baseURL: COWIN_BASE_URL,
                endpoint: "/v2/admin/location/states", method: HttpMethod.GET)
        Map response = HttpUtils.apiCaller(apiRequestInfo)
        if(response.statusCode == 200){
            Map rspBody = (Map) response.get('body')
            if(rspBody){
                states = (List<Map>)rspBody.get('states')
            }else {
                throw new Exception("Failed to fetch states: error : no response")
            }
        }else{
            throw new Exception("Failed to fetch states: response : ${response}")
        }
    }

    List<Map> getDistricts(String stateName){
        String stateId = states.find {it.state_name == stateName}?.state_id
        ApiRequestInfo apiRequestInfo = new ApiRequestInfo(baseURL: COWIN_BASE_URL,
                endpoint: "/v2/admin/location/districts/${stateId}", method: HttpMethod.GET)
        Map response = HttpUtils.apiCaller(apiRequestInfo)
        if(response.statusCode == 200){
            Map rspBody = (Map)response.get('body')
            if(rspBody) return (List)rspBody.get('districts')
        }else {
            log.error("Failed to get districts for state: ${stateName}")
            throw new Exception("Failed to get districts for state: ${stateName}")
        }
    }

    List<Map> getVaccineCenters(String state, String district){
        log.info("Fetching districts for state : $state!!!")
        List<Map> districts = getDistricts(state)
        log.info("Successfully fetched districts for state : $state")
        log.info("Districts : ${districts}")
        String district_Id = districts.find {it.district_name == district}?.get('district_id')
        if(district_Id){
            SimpleDateFormat DateFor = new SimpleDateFormat("dd-MM-yyyy")
            Map payload = [district_id : district_Id, date : DateFor.format(new Date()).toString()]
            ApiRequestInfo apiRequestInfo = new ApiRequestInfo(baseURL: COWIN_BASE_URL,
                    endpoint: "/v2/appointment/sessions/public/calendarByDistrict", method: HttpMethod.GET, payload:  payload)
            Map response = HttpUtils.apiCaller(apiRequestInfo)
            if(response.statusCode == 200){
                Map rspBody = (Map)response.get('body')
                if(rspBody) return (List)rspBody.get('centers')
            }else {
                log.error("Failed to get vaccination center for state: ${state} and district: ${district}")
                throw new Exception("Failed to get vaccination center for state: ${state} and district: ${district}")
            }
        }else {
            log.error("District $district does not exists!!!")
            throw new Exception("District $district does not exists!!!")
        }
    }

    @Scheduled(initialDelay = '${cowin.schedule.delay: 20s}', fixedRate = '${cowin.schedule.fixedRate: 5m}')
    void checkSlots(){
        log.info("Slot checking started for state ${state} and district ${{district}}.......")
        List<Map> vaccinationCenters = getVaccineCenters(state, district)
        if(vaccinationCenters.size() > 0){
            log.info("Vaccination centers avaliable in $district district")
            List<Map> nearByCenters = []
            // check slots for nearby places
            vaccinationCenters.each {Map center ->
                String blockNameLC = center.get('block_name').toString().toLowerCase()
                if(center.sessions && (nearByPlaces.contains(blockNameLC) ||
                        nearByPlaces.find {it.contains(blockNameLC) || blockNameLC.equals(it) || blockNameLC.contains(it)})){
                    nearByCenters << center
                }
            }
            if(nearByCenters){
                nearByCenters.each {Map center ->
                    boolean hasAtLeastOneSlot = false
                    String message = "Available vaccination slots at ${center.block_name} \n"
                    List<Map> sessions = (List<Map>) center.get("sessions")
                    sessions.each {Map session ->
                        if((int)session.available_capacity > 0){
                            message += "\n" + SlotInfo.getSlotInfo(center, session) + "\n"
                            hasAtLeastOneSlot = true
                        }
                    }
                    if(hasAtLeastOneSlot) telegramService.sendMessage(message)
                }
            }else {
                telegramService.sendMessage("No vaccination centers avaliable in nearby places : ${nearByPlaces.join(', ')}")
            }

        }else {
            log.info("No vaccination centers avaliable in state: $state district : $district")
            telegramService.sendMessage("No vaccination centers avaliable in state: $state district : $district")
        }
    }

}
