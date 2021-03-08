package com.gymer.commoncomponents.languagepack;

import com.gymer.commonresources.partner.entity.Partner;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;

public interface LanguageComponent {

    String testDataInitializedSuccessfully();

    String testDataNotInitialized();

    String youDontHaveRightsToDoThat();

    String partnerDoesntExists();

    String conflictWithIds();

    String invalidEmployee();

    String employeeChanged();

    String employeeRemoved();

    String mailSuccessfullySend();

    String cannotAuthorizeSingleSignIn();

    String cannotSignInViaOAuth2BecauseOfBeingPartner();

    String userNotLoggedViaSingleSignIn();

    String tooLateToDropVisit();

    String reservationRemoved();

    String userAlreadyExists();

    String alreadyReserved();

    String successfullyReservedNewSlot();

    String signInAsValidUser();

    String invalidSlotId();

    String getTitleFromUserToPartner();

    String getSmsTitleWhenSlotStartsInAnHour();

    String getMailNotificationWhenSlotStartsInAnHour(User user, Slot slot);

    String getSmsNotificationWhenSlotStartsInAnHour(User user, Slot slot);

    String getMessageFromUserToPartner(Partner partner, User user, String message);

}
