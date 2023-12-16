package pt.ulisboa.tecnico.bling_bank.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlingBankException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(BlingBankException.class);

    public BlingBankException(ErrorMessage errorMessage) {
        super(errorMessage.getLabel());
        logger.error(errorMessage.getLabel());
    }
}
