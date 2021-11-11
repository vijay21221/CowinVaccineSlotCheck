package com.example

/**
 * POGo to maintain Slot information
 */
class SlotInfo {
    String name
    String address
    String blockName
    String slotType
    String pincode
    String vaccine
    Integer totalSlots
    Integer slotsAvailableForDose1
    Integer slotsAvailableForDose2
    Integer ageLimit
    List<String> availableTimeSlots
    String availableDate

    @Override
    public String toString(){
        return "Name: $name\n"+
                "Block: ${blockName} \n" +
                "Address: $address \n" +
                "PinCode: $pincode \n" +
                "Vaccine: $vaccine \n" +
                "Date: $availableDate \n"+
                "Vaccine Payment Type: $slotType \n" +
                "Total Slots: $totalSlots \n" +
                "Slots Available For Dose1: $slotsAvailableForDose1 \n" +
                "Slots Available For Dose2: $slotsAvailableForDose2 \n" +
                "Age Limit: $ageLimit \n" +
                "Available times: ${availableTimeSlots.toString()} \n"
    }

    public static SlotInfo getSlotInfo(Map centerInfo, Map sessionInfo){
        SlotInfo slotInfo = new SlotInfo()
        slotInfo.setName(centerInfo.name)
        slotInfo.setAddress(centerInfo.address)
        slotInfo.setBlockName(centerInfo.block_name)
        slotInfo.setAgeLimit(sessionInfo.min_age_limit)
        slotInfo.setSlotType(centerInfo.fee_type)
        slotInfo.setVaccine(sessionInfo.vaccine)
        slotInfo.setTotalSlots(sessionInfo.available_capacity)
        slotInfo.setSlotsAvailableForDose1(sessionInfo.available_capacity_dose1)
        slotInfo.setSlotsAvailableForDose2(sessionInfo.available_capacity_dose2)
        slotInfo.setAvailableDate(sessionInfo.date)
        slotInfo.setPincode(centerInfo.pincode.toString())
        slotInfo.setAvailableTimeSlots((List<String>)sessionInfo.slots)
        return slotInfo
    }
}
