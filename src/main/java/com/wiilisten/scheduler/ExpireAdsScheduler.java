//package com.wiilisten.scheduler;
//
//import com.wiilisten.controller.BaseController;
//import com.wiilisten.entity.CallerProfile;
//import com.wiilisten.entity.ListenerProfile;
//import com.wiilisten.entity.User;
//import com.wiilisten.entity.UserSubscription;
//import com.wiilisten.enums.UserRoleEnum;
//import com.wiilisten.utils.ApplicationConstants;
//import com.wiilisten.utils.ServiceRegistry;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.List;
//
//@Component
//
//public class ExpireAdsScheduler extends BaseController {
//    private final static Logger LOGGER = LoggerFactory.getLogger(ExpireAdsScheduler.class);
//    @Autowired
//    private ServiceRegistry serviceRegistry;
//
//    // Every day at midnight
//    @Scheduled(cron = "0 0 0 * * ?")
////    @Scheduled(cron = "*/5 * * * * ?")
//    public void expireAdvertisements() {
//        LOGGER.info("ExpireAdsScheduler::expireAdvertisements Starting");
//
//        // Get current UTC time
//        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
//        LOGGER.info("Current UTC time: {}", nowUtc);
//
//        // Fetch all active ads where expiryDate < now (in UTC)
//        List<UserSubscription> activeAds = serviceRegistry
//                .getUserSubscriptionService()
//                .findByActiveTrueAndExpiryDateBefore(nowUtc);
//
//        LOGGER.info("Found {} active subscriptions to check for expiry.", activeAds.size());
//
//        for (UserSubscription ad : activeAds) {
//            LOGGER.info("Checking subscription ID: {}, Expiry: {}", ad.getId(), ad.getExpiryDate());
//
//            if (ad.getExpiryDate().isBefore(nowUtc)) {
//                LOGGER.info("Subscription ID: {} is expired. Marking inactive.", ad.getId());
//                ad.setActive(false);
//                serviceRegistry.getUserSubscriptionService().saveORupdate(ad);
//                LOGGER.info("Subscription ID: {} updated successfully.", ad.getId());
//            }
//            //updated user Profile
//            User user = ad.getUser();
//            if (user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
//                ListenerProfile listenerProfile = serviceRegistry.getListenerProfileService().findByUserAndActiveTrue(user);
//                listenerProfile.setIsAdvertisementActive(false);
//                listenerProfile.setIsEligibleForPremiumCallSearch(false);
//                getServiceRegistry().getListenerProfileService().saveORupdate(listenerProfile);
//            } else if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
//                CallerProfile callerProfile = serviceRegistry.getCallerProfileService().findByUserAndActiveTrue(user);
//                callerProfile.setSearchSubscriptionStatus(ApplicationConstants.EXPIRED);
//                getServiceRegistry().getCallerProfileService().saveORupdate(callerProfile);
//            }
//        }
//        LOGGER.info("ExpireAdsScheduler::expireAdvertisements Completed");
//    }
//
//
//}
