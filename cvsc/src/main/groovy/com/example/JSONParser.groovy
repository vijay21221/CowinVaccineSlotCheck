package com.example

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException


class JSONParser {
    static ObjectMapper parser = new ObjectMapper()

    static Map stringToMap(String jsonString){
        return parser.readValue(jsonString, Map.class)
    }

    static List stringToList(String jsonString){
        return parser.readValue(jsonString, List.class)
    }

    static String mapToString(Map object){
        return parser.writeValueAsString(object)
    }

    static String listToString(List object){
        return parser.writeValueAsString(object)
    }

    static def stringToMapORList(String jsonString){
        def filters
        try {
            filters = JSONParser.stringToMap(jsonString)
        }catch(MismatchedInputException e){
            try {
                filters = JSONParser.stringToList(jsonString)
            }catch(ex){
                // throw new IOException(ErrorCode.JSON_PARSE_FAILED, "Failed to parse JSON")
            }
        }catch(JsonParseException e){
            // throw new IOException(ErrorCode.JSON_PARSE_FAILED, "Failed to parse JSON")
        }
    }
}
