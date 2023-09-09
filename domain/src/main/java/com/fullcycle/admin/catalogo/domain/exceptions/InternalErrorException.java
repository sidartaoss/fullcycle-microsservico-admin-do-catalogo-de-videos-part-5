package com.fullcycle.admin.catalogo.domain.exceptions;

public class InternalErrorException extends NoStacktraceException {

    protected InternalErrorException(final String aMessage, final Throwable aCause) {
        super(aMessage, aCause);
    }

    public static InternalErrorException with(final String aMessage, final Throwable aCause) {
        return new InternalErrorException(aMessage, aCause);
    }

}
