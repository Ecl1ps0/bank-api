package com.bank_api.bank.utils;

import org.springframework.stereotype.Component;

import com.mifmif.common.regex.Generex;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class CardRequisiteGenerator {

    private final String visaCardNumberRegex = "4[0-9]{12}[0-9]{3}";
    private final String masterCardNumberRegex = "5[1-5][0-9]{14}";
    private final String ibanRegex = "KZ\\d{2}[A-Z0-9]{18}";
    private final String swiftRegex = "[A-Z]{4}KZ[A-Z0-9]{5}";

    public String generateVisaCardNumber() {
        return new Generex(visaCardNumberRegex).random();
    }

    public String generateMasterCardNumber() {
        return new Generex(masterCardNumberRegex).random();
    }

    public String generateIban() {
        return new Generex(ibanRegex).random();
    }

    public String generateSwift() {
        return new Generex(swiftRegex).random();
    }

}