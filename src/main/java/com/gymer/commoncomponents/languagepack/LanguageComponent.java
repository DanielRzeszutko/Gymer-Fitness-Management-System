package com.gymer.commoncomponents.languagepack;

import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.partner.entity.Partner;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;

public interface LanguageComponent {

    String testDataInitializedSuccessfully();

    String testDataNotInitialized();

    String successfullySynchronized();

    String youDontHaveRightsToDoThat();

    String partnerDoesntExists();

    String conflictWithIds();

    String invalidEmployee();

    String employeeChanged();

    String employeeRemoved();

    String cannotChangePassword();

    String passwordsDoesntEqual();

    String invalidNewPassword();

    String passwordChanged();

    String notImplementedYet();

    String alreadyVerified();

    String invalidVerificationCode();

    String successfullyVerified();

    String notValidAccount();

    String mailSuccessfullySend();

    String cannotAuthorizeSingleSignIn();

    String cannotSignInViaOAuth2BecauseOfBeingPartner();

    String userNotLoggedViaSingleSignIn();

    String tooLateToDropVisit();

    String reservationRemoved();

    String userAlreadyExists();

    String alreadyReserved();

    String alreadyTakenSlot();

    String alreadyFullSlot();

    String successfullyReservedNewSlot();

    String signInAsValidUser();

    String invalidSlotId();

    String getTitleFromUserToPartner();

    String fieldsCannotBeEmpty();

    String accountAlreadyExists();

    String activationMailSend();

    String registeredSuccessfully();

    String successfullyLoggedOut();

    String unknownError();

    String authenticationNotSupported();

    String usernameOrPasswordNotValid();

    String notVerified();

    String successfullyLoggedIn();

    String getSmsTitleWhenSlotStartsInAnHour();

    String getVerificationTitle();

    String getMailNotificationWhenSlotStartsInAnHour(User user, Slot slot);

    String getSmsNotificationWhenSlotStartsInAnHour(User user, Slot slot);

    String getMessageFromUserToPartner(Partner partner, User user, String message);

    String getVerificationEmail(Credential credential, String verifyURL);

}
