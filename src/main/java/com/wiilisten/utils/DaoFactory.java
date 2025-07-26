package com.wiilisten.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wiilisten.repo.*;

import lombok.Getter;

@Component
@Getter
public class DaoFactory {
    
    @Autowired
    private AdministrationRepository administrationRepository;
    
    @Autowired
    private AdministrativeNotificationRepository administrativeNotificationRepository;
    
    @Autowired
    private BlockedUserRepository blockedUserRepository;
        
    @Autowired
    private BookedCallsRepository bookedCallsRepository;

    @Autowired
    private CallerProfileRepository callerProfileRepository;

    @Autowired
    private CardDetailsRepository cardDetailsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommissionRateRepository commissionRateRepository;

    @Autowired
    private ContactUsRepository contactUsRepository;

    @Autowired
    private EarningHistoryRepository earningHistoryRepository;

    @Autowired
    private FaqRepository faqRepository;

    @Autowired
    private FavouriteListenerRepository favouriteListenerRepository;

    @Autowired
    private ListenerAvailabilityRepository listenerAvailabilityRepository;

    @Autowired
    private ListenerBankDetailsRepository listenerBankDetailsRepository;

    @Autowired
    private ListenerProfileRepository listenerProfileRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationHistoryRepository notificationHistoryRepository;

    @Autowired
    private OpenEndedQuestionRepository openEndedQuestionRepository;

    @Autowired
    private OtpHistoryRepository otpHistoryRepository;

    @Autowired
    private PageContentRepository pageContentRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private TrainingMaterialRepository trainingMaterialRepository;

    @Autowired
    private UserRatingAndReviewRepository userRatingAndReviewRepository;
    
    @Autowired
    private AdminModulePermissionRepository adminModulePermissionRepository;
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    
    @Autowired
    private AdminModuleRepository adminModuleRepository;
    
    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;
    
    @Autowired
    private NewsLetterRepository newsLetterRepository;
    
    @Autowired
    private CouponRepository couponRepository;
    
    @Autowired
    private ListenerAnalyticRepo listenerAnalyticRepo;
    
    @Autowired
    private UserNotesRepository userNotesRepository;
    
}
