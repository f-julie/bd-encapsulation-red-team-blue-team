package com.amazon.ata.encapsulation.model;

import com.amazon.ata.encapsulation.model.PaymentInstrumentType;

import java.math.BigDecimal;

/**
 * The class representing a payment instrument (e.g. a particular customer's gift or credit card).
 */
public class PaymentInstrument {
    private String instrumentId;
    private BigDecimal availableFunds = new BigDecimal("0");
    private PaymentInstrumentType instrumentType = PaymentInstrumentType.CREDIT_CARD;

    /**
     * Constructs a PaymentInstrument with a provided unique ID.
     *
     * @param id A unique identifier for the PaymentInstrument.
     */
    public PaymentInstrument(final String id) {
        this.instrumentId = id;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public BigDecimal getAvailableFunds() {
        return availableFunds;
    }

    public void setAvailableFunds(final BigDecimal availableFunds) {
        this.availableFunds = availableFunds;
    }

    public PaymentInstrumentType getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(PaymentInstrumentType type) {
        instrumentType = type;
    }

    @Override
    public boolean equals(Object obj) {

        // Check that obj is not null and is a PaymentInstrument
        if (!(obj instanceof PaymentInstrument)) {
            return false;
        }

        // Cast it as a PaymentInstrument so we can use its methods and fields
        final PaymentInstrument other = (PaymentInstrument) obj;

        // If ID is null, other must also be null
        if (instrumentId == null) {
            return other.instrumentId == null;
        }

        // Otherwise they must be identical
        return instrumentId.equals(other.instrumentId);
    }

    @Override
    public String toString() {
        return "PaymentInstrument{" +
            "instrumentId='" + instrumentId + '\'' +
            ", availableFunds=" + availableFunds +
            ", instrumentType=" + instrumentType +
            '}';
    }
}
