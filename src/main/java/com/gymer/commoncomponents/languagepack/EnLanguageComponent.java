package com.gymer.commoncomponents.languagepack;

import com.gymer.commonresources.partner.entity.Partner;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class EnLanguageComponent implements LanguageComponent {

    public String testDataInitializedSuccessfully() {
        return "Test data initialized.";
    }

    public String testDataNotInitialized() {
        return "Error with initializing test data.";
    }

    public String youDontHaveRightsToDoThat() {
        return "You don't have rights to do that.";
    }

    public String partnerDoesntExists() {
        return "Selected partner don't exists.";
    }

    public String conflictWithIds() {
        return "Conflict with id or URL";
    }

    public String mailSuccessfullySend() {
        return "Mail successfully send.";
    }

    public String invalidSlotId() {
        return "Invalid slot Id";
    }

    public String cannotAuthorizeSingleSignIn() {
        return "Can't authorize single sign-in.";
    }

    public String userNotLoggedViaSingleSignIn() {
        return "User not logged by single Sign-In account.";
    }

    public String tooLateToDropVisit() {
        return "You can't drop visit now, too late.";
    }

    public String signInAsValidUser() {
        return "Sign in as valid user";
    }

    public String userAlreadyExists() {
        return "User with this email already exists.";
    }

    public String invalidEmployee() {
        return "Can't add Employee.";
    }

    public String employeeRemoved() {
        return "Successfully removed Employee from slot.";
    }

    public String employeeChanged() {
        return "Successfully changed Employee in Slot.";
    }

    public String alreadyReserved() {
        return "Already reserved this slot.";
    }

    public String successfullyReservedNewSlot() {
        return "Successfully added reservation details.";
    }

    public String reservationRemoved() {
        return "Successfully removed reservation.";
    }

    public String cannotSignInViaOAuth2BecauseOfBeingPartner() {
        return "You can't login via single Sign-In because you already have partner's account. Please use your standard account.";
    }

    public String getMailNotificationWhenSlotStartsInAnHour(User user, Slot slot) {
        String userDetails = user.getFirstName() + " " + user.getLastName();
        return "Dear " + userDetails + ",<br>"
                + "Your slot " + slot.getSlotType() + "starting in an hour.<br>"
                + "Details: " + slot.getDescription() + "<br>"
                + "Thank you for your support,<br>"
                + "Team Gymer.";
    }

    public String getSmsNotificationWhenSlotStartsInAnHour(User user, Slot slot) {
        return "Hi " + user.getFirstName() + " " + user.getLastName() + ".\n"
                + "Your slot starts at " + slot.getStartTime() + ".\n"
                + slot.getSlotType() + " with " + slot.getEmployee().getFirstName() + " " + slot.getEmployee().getLastName() + ".\n"
                + "Team Gymer!";
    }

    public String getMessageFromUserToPartner(Partner partner, User user, String message) {
        String userDetails = user.getFirstName() + " " + user.getLastName();
        String userEmail = user.getCredential().getEmail();
        String userPhone = user.getCredential().getPhoneNumber();

        return "Dear " + partner.getName() + ",<br>"
                + "You have new question from " + userDetails + "<br>"
                + message
                + "To contact user use below credentials:"
                + "Email: " + userEmail + " or phone number: " + userPhone
                + "Thank you for your support,<br>"
                + "Team Gymer.";
    }

    public String getTitleFromUserToPartner() {
        return "You have new question from user.";
    }

    public String getSmsTitleWhenSlotStartsInAnHour() {
        return "Your slot starting in an hour.";
    }

}
