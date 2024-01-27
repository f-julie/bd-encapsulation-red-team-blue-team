# Red Team Blue Team Activities 1 & 2

# Activity 1 : RED TEAM

**GitHub repo:** [ebd-encapsulation-red-team-blue-team](https://github.com/LambdaSchool/ebd-encapsulation-red-team-blue-team)

## Preliminaries: getting the lay of the land

1. **UML Diagram:** Examine the payment system class diagram along with the instructor.

   Notice that this simplified payment system supports gift cards and credit cards (payment instrument types).
   Many systems will call `PaymentSelector::getPreferredPaymentInstrument()` to determine how to pay for an order for
   a particular customer. It uses `CustomerPaymentInstruments` to select the appropriate payment method.

1. **PaymentSelector call stack:** Read the code in the com.amazon.ata package,
   `encapsulation` java package, starting with `PaymentSelector`, tracing through the method calls
   from `getPreferredPaymentInstrument`. Be prepared to answer questions about what each class is responsible for.

1. **Diving Deeper:** Look at `CustomerPaymentInstruments` and `PaymentInstruments` in particular.
    1. Are they properly encapsulated?
    1. If not, why not?

1. **How the attacks are structured:** Look at the Attacker_CD.puml diagram showing
   the test class, `PaymentSelectorTest`, and its partner in crime,
   `Attacker`.

1. **Revealing the attacks:** Now look at `PaymentSelectorTest` (in the tst/ directory of course!), and its first
   test method.
    1. Run the first test: what happens?
    1. What is the test doing?
    1. What is the role of the `Attacker` class?
    1. What is the bug?
    1. How is the `Attacker` code causing the test to fail?

   If an evil genius found a way to the `Attacker` class in Amazon, how else could they attack the payment
   selection code?

## Plan of Attack!

1. Your job here is to implement methods in `Attacker` that exploit the poor encapsulation of the classes in the
<<<<<<< HEAD
   `com.amazon.ata.encapsulation.model` package. The
=======
   `com.amazon.ata.encapulation.model` package. The
>>>>>>> b4439d2a9cf55f03523558e08762f2109a21dc34
   `PaymentSelectorTest` class will make use of these methods to
   cause trouble.

1. The "attack" tests in `PaymentSelectorTest` already call these methods, so when you get your attack correct,
   the test should start failing with a message that includes "RED TEAM!" in it.

1. Before writing code, discuss your attack ideas with your group.
   Three or four evil geniuses find more exploits than one!
   Try to think of several different ways to attack the poorly
   encapsulated tasks...don't just rely on the same
   trick (especially not the same as in the starter code! :) )

### Sample

We've done the first one for you! Go to the `PaymentSelectorTest` class
under tst/com/amazon/ata/encapsulation/service and run
the unit tests. You should see that
`attackGetPreferredPaymentInstrument_whenAttackTriesToDecreaseAvailableFunds_returnsOriginalInstrument`
is already failing with a message that includes "RED TEAM" in it.

You'll want to make the other "attack" tests in this test class fail
in similar ways. Make sure "RED TEAM" shows up in the failure messages!

## Attack!

Ok, implement some attacks! (feel free to discuss/troubleshoot with your group as you go)

### Ground rules:

1. Don't change `PaymentSelectorTest`, except to add test methods to it for the extensions
1. No modifications at all to `PaymentSelector`, `CustomerPaymentInstruments` or `PaymentInstrument`
1. Only modify `Attacker`, first by implementing the given methods commented with "PARTICIPANTS", then
   you can add attack methods if you get that far through the extensions.
1. Your tests must fail with the "RED TEAM" message...don't just go throwing a `NullPointerException` somewhere.

#### Expectations:

1. Implement the attack marked "COMPLETION 1" (`replaceFirstInstrumentWithMoreFunds`)
   in `Attacker` and ensure that the corresponding test fails with
   "RED TEAM" in the error message
1. Implement the attack marked "COMPLETION 2" (`reorderPaymentInstruments`) in `Attacker` and ensure that the
   corresponding test fails with "RED TEAM" in the error message

When implementing these attacks, you might want to take a look at the bottom of `PaymentSelectorTest`, as that
defines the customers (and their payment instruments) that the test will be using to verify your RED TEAM attacks.
You only need to make the attacks work for the specific customers
in the test code (not a completely general solution).

## Extensions:

If you've completed the above, keep going! Again, use different attacks from
the ones you used above. Try to follow the hints/suggestions in the
code (and in error messages if they don't include "RED TEAM")

1. Implement the attack marked "EXTENSION 1"
1. Implement the attack marked "EXTENSION 2"
1. Implement the attack marked "EXTENSION 3", name it, and add the corresponding test. For this extension, you come
   up with your own exploit.
1. Think of other exploits, add attacks and tests

## Goal

Get PaymentSelectorTest's attackGetPReferredPaymentInstrument...()
tests to start failing with messages that include "RED TEAM" in
them. First the tests labeled "COMPLETION", then those with "EXTENSION".

## Commit & Push

1. When you have the code to where you want it (at least compiling, ideally "RED TEAM"ting), commit it
1. Push it to your remote branch
1. Go back to the RED TEAM page on Canvas and paste in a link to your commit


# Activity 2 : Blue team

Now, take the role of the opposing force: your job is now to protect the payment instrument selection logic from
the attacks you just deployed!

## Plan

1. What can Amazon do about these exploits?
   Write down the fixes you would make to `CustomerPaymentInstruments` and/or `PaymentInstrument`
   * See if you can figure out a way to get some of the tests passing
     **without fixing the `whenAttackTriesToDecreaseAvailableFunds`
     attack. See if you can implement this before implementing
     the rest of your fixes. Be prepared to discuss this fix with
     the class, why it fixes this test but not the others.

## Ground rules

* Only change `CustomerPaymentInstruments` and/or `PaymentInstrument`
* Leave all of your attacks in place! Don't change the `Attacker` class that made
  the "RED TEAM" failure messages appear

## Goal

Refactor `CustomerPaymentInstruments` and `PaymentInstrument`
to protect the system from misuse. When you fix a vulnerability,
run the tests. The test for that vulnerability should now pass.

### Commit & Push

1. When you have the code to where you want it (at least compiling, ideally all tests passing), commit it
1. Push it to your remote branch
1. Go back to the Blue Team canvas page and paste in a link to your commit

## Discuss

Discuss what you implemented, and what you learned with the class.
* Were all of your fixes needed to get the tests to pass?
* Could your code pass all the tests written by other groups?
* What other fixes would you recommend for complete encapsulation? Were there any ways to exploit those?
