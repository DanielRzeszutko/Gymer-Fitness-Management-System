package com.gymer.commoncomponents.languagepack;

import com.gymer.commonresources.credential.entity.Credential;
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

    public String cannotChangePassword() {
        return "You can't change your password.";
    }

    public String passwordsDoesntEqual() {
        return "Passwords are not equal. Please provide your old password.";
    }

    public String notImplementedYet() {
        return "Not implemented yet!";
    }

    public String alreadyVerified() {
        return "Account is already verified. Please login.";
    }

    public String invalidVerificationCode() {
        return "Sorry, verification code is incorrect. Please try again";
    }

    public String successfullyVerified() {
        return "Account has been verified successfully";
    }

    public String notValidAccount() {
        return "Not logged in as valid User.";
    }

    public String invalidNewPassword() {
        return "Invalid new password, please enter minimum 6 characters.";
    }

    public String passwordChanged() {
        return "Successfully changed password";
    }

    public String fieldsCannotBeEmpty() {
        return "Fields cannot be empty!";
    }

    public String accountAlreadyExists() {
        return "Account with this email already exists.";
    }

    public String successfullyLoggedOut() {
        return "Successfully logged out.";
    }

    public String notVerified() {
        return "Account not activated.";
    }

    public String unknownError() {
        return "Unknown error, sorry. Please be patient.";
    }

    public String authenticationNotSupported() {
        return "Authentication method not supported: ";
    }

    public String usernameOrPasswordNotValid() {
        return "Username or password is not valid.";
    }

    public String successfullyLoggedIn() {
        return "Successfully logged in.";
    }

    public String activationMailSend() {
        return "Activation email resend. Please check your email to verify your account.";
    }

    public String registeredSuccessfully() {
        return "Registered successfully. Please check your email to verify your account.";
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

    public String getVerificationEmail(Credential credential, String verifyURL) {
        String content = "Dear " + credential.getRole() + ",<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Team Gymer.";
        return content.replace("[[URL]]", verifyURL);
    }

    public String getTitleFromUserToPartner() {
        return "You have new question from user.";
    }

    public String getSmsTitleWhenSlotStartsInAnHour() {
        return "Your slot starting in an hour.";
    }

    public String getVerificationTitle() {
        return "Please verify your registration";
    }

}
