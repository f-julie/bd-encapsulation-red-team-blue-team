package com.amazon.ata.encapsulation.service;

import com.amazon.ata.encapsulation.exploit.Attacker;
import com.amazon.ata.encapsulation.model.CustomerPaymentInstruments;
import com.amazon.ata.encapsulation.model.PaymentInstrument;
import com.amazon.ata.encapsulation.model.Customer;
import com.amazon.ata.encapsulation.model.PaymentInstrumentType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Performs several tests on the PaymentSelector::getPreferredPaymentInstrument method.
 * <p>
 * The first few tests begin with "attackGetPreferredPaymentInstrument_", which are tests
 * that verify that specific attacks do *NOT* succeed. However, your job is to first
 * implement methods in Attacker that perform the attacks and get the tests to fail in
 * specific ways. You'll later go on to fix them....
 */
class PaymentSelectorTest {
    private PaymentInstrument customer1GiftCard;
    private PaymentInstrument customer1CreditCard;
    private PaymentInstrument customer2GiftCard;
    private PaymentInstrument customer2CreditCard;
    private CustomerPaymentInstruments customer1Prefs;
    private PaymentSelector customer1Selector;

    private CustomerPaymentInstruments customer2Prefs;
    private PaymentSelector customer2Selector;

    private Attacker attacker = new Attacker();

    /**
     * Configures our test environment to look like the Amazon environment.
     * Assumes there are a couple of customers with different payment preferences.
     */
    @BeforeEach
    void setUp() {
        // Set up customer1, who prefers to pay with his $100 gift card, then his $1000 credit card
        createCustomer1();

        // Set up customer2, who prefers to pay with his $5000 credit card, then his $5 gift card
        createCustomer2();
    }


    // Attack tests

    /**
     * This test checks whether PaymentSelector returns the appropriate payment instrument after
     * a call to depleteFirstInstrumentFunds, which sets the available funds on the first customer payment instrument
     * to -1.
     * <p>
     * The attack is implemented for you, so this should fail from the get-go. Follow a similar pattern by implementing
     * the methods in Attacker that correspond to the tests below, then come back to see them fail.
     * <p>
     * In BLUE TEAM mode, the test should pass.
     */
    @Test
    void attackGetPreferredPaymentInstrument_whenAttackTriesToDecreaseAvailableFunds_returnsOriginalInstrument() {
        // GIVEN
        // An order of $100, which could be fulfilled by Customer1's gift card
        BigDecimal orderAmount = new BigDecimal(100);
        final PaymentInstrument correctInstrument = customer1Selector.getPreferredPaymentInstrument(orderAmount);

        // WHEN
        // An attacker tries to change Customer1's gift card funds
        attacker.depleteFirstInstrumentFunds(customer1Prefs);
        final PaymentInstrument selectedInstrument = customer1Selector.getPreferredPaymentInstrument(orderAmount);

        // THEN
        // Before the attack, the gift card was selected because it covered the order amount
        assertEquals(customer1GiftCard, correctInstrument, "Customer should have been able to pay with gift card!");
        // RED TEAM: attacker tries to deplete the funds on the gift card...
        assertNotEquals(customer1CreditCard, selectedInstrument,
                        "RED TEAM! $$$ -----> $ : An attacker depleted gift card funds!");
        // The attack failed, and the gift card was still selected
        assertEquals(customer1GiftCard, selectedInstrument, "Attacker depleted gift card funds!");
    }

    /**
     * COMPLETION 1:
     * <p>
     * This test checks whether PaymentSelector returns the appropriate payment instrument after
     * a call to inflateFirstInstrumentFunds.
     * <p>
     * In RED TEAM mode, without changing this test code at all, see if you can get the test to fail with the message
     * including "RED TEAM"
     * <p>
     * In BLUE TEAM mode, the test should pass.
     */
    @Test
    void attackGetPreferredPaymentInstrument_whenAttackTriesToReplaceInstrumentWithMoreFunds_returnsOriginalInstrument() {
        // GIVEN
        // An order of $500, which could NOT be fulfilled by Customer1's gift card
        BigDecimal orderAmount = new BigDecimal(500);
        final PaymentInstrument correctInstrument = customer1Selector.getPreferredPaymentInstrument(orderAmount);

        // WHEN
        // An attacker attempts inflateFirstInstrumentFunds
        attacker.replaceFirstInstrumentWithMoreFunds(customer1Prefs);
        final PaymentInstrument selectedInstrument = customer1Selector.getPreferredPaymentInstrument(orderAmount);

        // check for 'wrong' ways to attack...
        if (selectedInstrument == customer1GiftCard) {
            // if your test fails on this line, looks like you might be trying
            // the same exploit as the sample we wrote for you. See if you
            // can get this test to fail with the "RED TEAM" message by
            // replacing the PaymentInstrument entirely.
            fail("Don't try modifying the existing PaymentInstrument in place. Try replacing!");
        }
        if (!selectedInstrument.getInstrumentId().equals(customer1GiftCard.getInstrumentId()) &&
            !selectedInstrument.getInstrumentId().equals(correctInstrument.getInstrumentId())) {
            // if your tests fail on this line, looks like you're taking a
            // different approach to the attack. See if you can get this
            // test to fail with the "RED TEAM" message by replacing the zeroth
            // PaymentInstrument with a new instance, but only change the
            // funds available, nothing else.
            fail("Found an unexpected instrument ID, you may have inadvertently perpetrated a " +
                 "different attack: selectedInstrument.getInstrumentId()");
        }

        // THEN
        // RED TEAM: See if you can get the selected instrument to be customer 1's gift card,
        //            even though it doesn't start off with sufficient funds available.
        //            Don't try to accomplish this by reordering the instruments.
        assertNotEquals(customer1GiftCard.getInstrumentId(), selectedInstrument.getInstrumentId(),
                        "RED TEAM! $ -----> $$$ : An attacker purchased more than gift card contained!");
        // The same card was selected both times
        assertEquals(correctInstrument,
                     selectedInstrument,
                     "Customer should have been able to pay with the " +
                     "same card before and after inflateFirstInstrumentFunds!");
    }

    /**
     * COMPLETION 2:
     * <p>
     * This test checks whether PaymentSelector returns the appropriate payment instrument after
     * a call to reorderPaymentInstruments.
     * <p>
     * In RED TEAM mode, without changing this test code at all, see if you can get the test to fail with the message
     * including "RED TEAM"
     * <p>
     * In BLUE TEAM mode, the test should then start passing.
     */
    @Test
    void attackGetPreferredPaymentInstrument_whenAttackTriesToReorderInstruments_returnsOriginalInstrument() {
        // GIVEN
        // An order of $1, which can be fulfilled by Customer1's gift card
        BigDecimal orderAmount = new BigDecimal(1);
        final PaymentInstrument correctInstrument = customer1Selector.getPreferredPaymentInstrument(orderAmount);
        PaymentInstrument giftCard = customer1Prefs.getPaymentInstruments()[0];
        BigDecimal originalGiftCardFunds = giftCard.getAvailableFunds();

        // WHEN
        // An attacker attempts reorderPaymentInstruments
        attacker.reorderPaymentInstruments(customer1Prefs);
        final PaymentInstrument selectedInstrument = customer1Selector.getPreferredPaymentInstrument(orderAmount);

        // check for 'wrong' ways to attack...
        if (selectedInstrument != customer1CreditCard && !selectedInstrument.equals(correctInstrument)) {
            // if your test fails on this line, make sure you're just reordering
            // existing PaymentInstruments, and not creating new ones.
            fail("Be sure you're using one of the existing payment instruments");
        }
        if (customer1Prefs.getPaymentInstruments()[0].equals(giftCard) &&
            !customer1Prefs.getPaymentInstruments()[0].getAvailableFunds().equals(originalGiftCardFunds)) {
            // if your test fails on this line, make sure you're actually reordering
            // PaymentInstruments, and not just hacking the available funds as in
            // the first attack.
            fail("Be sure you're not trying to modify the gift card available funds...");
        }

        // THEN
        // RED TEAM: See if you can reorder customer 1's payment instruments so that the credit card is returned
        assertNotEquals(customer1CreditCard, selectedInstrument,
                        "RED TEAM! $$ <===x===> $$ : An attacker reordered a " +
                        "customer's preferred instruments!");
        // The same card was selected both times
        assertEquals(correctInstrument, selectedInstrument,
                     "Customer should have been able to pay with the same card " +
                     "before and after reorderPaymentInstruments!");
    }

    /**
     * EXTENSION 1:
     * <p>
     * This test checks whether PaymentSelector returns the appropriate payment instrument after
     * a call to grantCustomerAccessToAnotherCustomersInstruments.
     * <p>
     * In RED TEAM mode, without changing this test code at all, see if you can get the test to fail with the message
     * including "RED TEAM"
     * <p>
     * In BLUE TEAM mode, the test should then start passing.
     */
    @Test
    void attackGetPreferredPaymentInstrument_whenAttackTriesToUseOtherCustomersInstruments_returnsOriginalInstrument() {
        // GIVEN
        // An order of $100, which could be fulfilled by Customer1's gift card
        BigDecimal orderAmount = new BigDecimal(100);
        final PaymentInstrument correctInstrument = customer1Selector.getPreferredPaymentInstrument(orderAmount);

        // WHEN
        // An attacker attempts grantCustomerAccessToAnotherCustomersInstruments
        attacker.grantCustomerAccessToAnotherCustomersInstruments(customer1Prefs, customer2Prefs);
        final PaymentInstrument selectedInstrument = customer1Selector.getPreferredPaymentInstrument(orderAmount);

        // THEN
        // RED TEAM: See if you can get customer 1 to be able to pay with customer 2's credit card, even
        //            though it belongs to customer2
        assertNotEquals(customer2CreditCard.getInstrumentId(), selectedInstrument.getInstrumentId(),
                        "RED TEAM! <(o.O)> $$ -----> ¯\\_(ツ)_/¯ : An attacker allowed one customer to pay with " +
                            "another customer's credit card!");
        // The same card was selected both times
        assertEquals(correctInstrument, selectedInstrument,
                     "Customer should have been able to pay with the same card before and after grantCustomerAccessToAnotherCustomersInstruments!");
    }

    /**
     * EXTENSION 2:
     * <p>
     * This test checks whether PaymentSelector returns the appropriate payment instrument after
     * a call to destroyPaymentInstruments.
     * <p>
     * In RED TEAM mode, without changing this test code at all, see if you can get the test to fail with the message
     * including "RED TEAM"
     * <p>
     * In BLUE TEAM mode, the test should then start passing.
     */
    @Test
    void attackGetPreferredPaymentInstrument_whenAttackTriesToDestroyInstruments_returnsOriginalInstrument() {
        // GIVEN
        // An order of $100, which can be fulfilled by Customer1's gift card
        BigDecimal orderAmount = new BigDecimal(100);
        final PaymentInstrument correctInstrument = customer1Selector.getPreferredPaymentInstrument(orderAmount);
        PaymentInstrument[] originalInstruments = customer1Prefs.getPaymentInstruments();

        // WHEN
        // An attacker attempts destroyPaymentInstruments
        attacker.destroyPaymentInstruments(customer1Prefs);
        final PaymentInstrument selectedInstrument = customer1Selector.getPreferredPaymentInstrument(orderAmount);

        // check for 'wrong' ways to attack
        if (selectedInstrument == null) {
            for (PaymentInstrument paymentInstrument : customer1Prefs.getPaymentInstruments()) {
                // if the test fails on this line, you may have modified the
                // PaymentInstruments instead of deleting them (setting to null)
                assertNull(paymentInstrument,
                           "Expected attack to nullify all PaymentInstruments " +
                           "but at least one remains: " + paymentInstrument);
            }
        }

        // THEN
        // RED TEAM: See if you can destroy a customer's preferred payment instruments, so that their purchase
        //            will be denied.
        assertNotNull(selectedInstrument,
                      "RED TEAM! $$$ --X--> !!! : An attacker rendered customer's preferred instruments useless!");
        // The same card was selected both times
        assertEquals(correctInstrument, selectedInstrument,
                     "Customer should have been able to pay with the same card before and after " +
                         "destroyPaymentInstruments!");
    }

    /**
     * EXTENSION 3:
     *
     * This test checks whether PaymentSelector returns the appropriate payment instrument after a call to "attack6."
     *
     * In BLUE TEAM mode, the test should then start passing.
     */
    // PARTICIPANT: Add a new test here, using your new attack! You don't have to add the "RED TEAM!" assertion,
    //              but you should ensure that the test is failing for the expected reason before fixing.


    // PARTICIPANT: Extension 3+: Add more tests here if you create more attacks.


    // "normal" / non-attack tests:

    /**
     * Verifies that the PaymentSelector returns null if no card can cover the order amount.
     */
    @Test
    void getPreferredPaymentInstrument_returnsNull_whenNoInstrumentIsSufficient() {
        // GIVEN
        // An order that no payment instrument can cover
        BigDecimal orderAmount = new BigDecimal(10_000);

        // WHEN
        // Asked to select a payment instrument
        final PaymentInstrument result = customer1Selector.getPreferredPaymentInstrument(orderAmount);

        // THEN
        // The selector returns null
        assertNull(result, "Selector should return null when order is too large for all payment instruments!");
    }

    /**
     * Verifies that multiple calls to select a PaymentInstrument will return the same value.
     * <p>
     * Sometimes we want to guarantee that something gets called, so we're willing to call it many times to make sure it
     * happens "at least once". Sometimes we make the same call multiple separate times, like checking to see if there's
     * enough funds to check out and then checking to see if there's enough funds before we ship. Sometimes many
     * systems with different responsibilities each make the same call. A method that returns the same value when it's
     * given the same inputs, regardless of how many times it's called, is known as an "idempotent" method.
     * <p>
     * This test verifies that the PaymentSelector::getPreferredPaymentInstrument() is idempotent.
     */
    @Test
    void getPreferredPaymentInstrument_prefersSameCard_whenCalledMultipleTimes() {
        // GIVEN
        // An order of $100
        BigDecimal orderAmount = new BigDecimal(100);

        // WHEN
        // Asked to select a payment instrument twice for the same order
        final PaymentInstrument choice1 = customer1Selector.getPreferredPaymentInstrument(orderAmount);
        final PaymentInstrument choice2 = customer1Selector.getPreferredPaymentInstrument(orderAmount);

        // THEN
        // We always return the same instrument
        assertEquals(choice1, choice2, "Multiple calls should return the same PaymentInstrument!");
    }

    // Helper functions

    private void createCustomer2() {
        final Customer customer2 = new Customer("c2", "Nahn N. Capsulated");
        final PaymentInstrument[] customer2Instruments = new PaymentInstrument[2];
        customer2CreditCard = setupPaymentInstrument("c2i2", PaymentInstrumentType.CREDIT_CARD, 5000);
        customer2GiftCard = setupPaymentInstrument("c2i1", PaymentInstrumentType.GIFT_CARD, 5);
        customer2Instruments[0] = customer2CreditCard;
        customer2Instruments[1] = customer2GiftCard;
        customer2Prefs = new CustomerPaymentInstruments(customer2, customer2Instruments);
        customer2Selector = new PaymentSelector(customer2Prefs);
    }

    private void createCustomer1() {
        final Customer customer1 = new Customer("c1", "Minerva N. Kapsulation");
        customer1GiftCard = setupPaymentInstrument("c1i1", PaymentInstrumentType.GIFT_CARD, 100);
        customer1CreditCard = setupPaymentInstrument("c1i2", PaymentInstrumentType.CREDIT_CARD, 1000);
        final PaymentInstrument[] customer1Instruments = new PaymentInstrument[2];
        customer1Instruments[0] = customer1GiftCard;
        customer1Instruments[1] = customer1CreditCard;
        customer1Prefs = new CustomerPaymentInstruments(customer1, customer1Instruments);
        customer1Selector = new PaymentSelector(customer1Prefs);
    }

    /**
     * Helper function making creating a PaymentInstrument more readable. In the actual Amazon environment, you would
     * expect PaymentInstrument to have a constructor or builder to do this; sometimes you see patterns like this when
     * different teams are responsible for different parts of the object, so they have to be created early on and
     * populated later.
     *
     * @param id    The ID of the PaymentInstrument.
     * @param type  The type of the PaymentInstrument.
     * @param funds The funds currently available to this PaymentInstrument.
     * @return A fully populated PaymentInstrument.
     */
    private PaymentInstrument setupPaymentInstrument(final String id, final PaymentInstrumentType type,
                                                     final long funds) {
        final PaymentInstrument instrument = new PaymentInstrument(id);
        instrument.setInstrumentType(type);
        instrument.setAvailableFunds(new BigDecimal(funds));

        return instrument;
    }
}
