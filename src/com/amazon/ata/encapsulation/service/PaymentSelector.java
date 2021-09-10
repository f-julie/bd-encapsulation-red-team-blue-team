package com.amazon.ata.encapsulation.service;

import com.amazon.ata.encapsulation.model.CustomerPaymentInstruments;
import com.amazon.ata.encapsulation.model.PaymentInstrument;

import java.math.BigDecimal;

/**
 * The class that chooses which payment instrument to use for an order. The
 * <code>getPreferredPaymentInstrument()</code> method is called many times
 * for each order. Since it uses the unencapsulated CustomerPaymentInstruments,
 * an attacker could make it fail without changing its code.
 * <p>
 * DO NOT MODIFY THIS CLASS.
 * <p>
 * Assume this code belongs to another team and you have read-only access.
 * <p>
 * Take the role of an attacker who can write and execute new code in Amazon's
 * systems. Write code in the <code>Attacker</code> class that can make this
 * method fail its tests.
 */
public class PaymentSelector {
    private CustomerPaymentInstruments customerPrefs;

    /**
     * Constructs a PaymentSelector with the provided in-order customer
     * preferred payment instruments.
     *
     * @param customerPrefs The payment instruments preferred by the customer,
     *                      in order.
     */
    public PaymentSelector(final CustomerPaymentInstruments customerPrefs) {
        this.customerPrefs = customerPrefs;
    }

    /**
     * Find the most suitable PaymentInstrument for the given order.
     *
     * @param orderTotal The total amount to be covered by the payment instrument.
     * @return The first payment instrument with sufficient funds to cover the orderTotal, or null if none exists.
     */
    public PaymentInstrument getPreferredPaymentInstrument(final BigDecimal orderTotal) {
        final PaymentInstrument[] instruments = customerPrefs.getPaymentInstruments();
        for (final PaymentInstrument instrument : instruments) {
            if (null == instrument) {
                continue;
            }

            if (instrument.getAvailableFunds().compareTo(orderTotal) >= 0) {
                return instrument;
            }
        }
        return null;
    }

}
