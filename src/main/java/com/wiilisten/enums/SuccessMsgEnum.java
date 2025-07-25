package com.wiilisten.enums;

/**
 * The <code>SuccessMsgEnum</code> is used to hold all the success key enums for
 * success messages to send in response in the application.
 *
 * @author Hyperlink Infosystem
 *
 */
public enum SuccessMsgEnum {

	INVALID_TOKEN_MESSAGE("invalid.token"),
	STATUS_CHANGED("status.change"),
	USERNAME_NOT_EXITS("username.not.exits"),
	ACCEPT_STATUS_CHANGED("accept.status.change"),
	REJECT_STATUS_CHANGED("reject.status.change"),
	RESCHEDUALE_STATUS_CHANGED("rescheduale.status.change"),
	FAVORITE_NOT_FOUND("favorite.not.found"),
	SPONSORED_NOT_FOUND("sponsored.not.found"),
	WITHDRAW_STATUS_CHANGED("withdraw.status.change"),
	REQUEST_STATUS_CHANGED("request.status.changed"),
	REQUEST_SENT("request.sent"),
	LISTNER_ADD_FAVORITE("add.favorite"),
	LISTNER_REMOVE_FAVORITE("remove.favorite"),
	OTP_SENT_PHONE_SUCCESS("otp.sent.phone.success"), 
	OTP_SENT_EMAIL_SUCCESS("otp.sent.email.success"),
	OTP_SENT_SUCCESS("otp.sent.success"),
	OTP_VERIFIED("otp.verified"),
	USER_ADDED("user.added"), USER_UPDATED("user.updated"),ADMIN_UPDATED_SUCCESSFULLY("admin.updated.successfully"),
	PROFILE_UPDATE("profile.update"),
	LOGIN_SUCCESS_MESSAGE("login.success.message"),
	PASSWORD_CHANGED_SUCCESSFULLY("password.changed.successfully"),
	NOTIFICATION_STATUS_UPDATED_SUCCESSFULLY("notification.status.updated.successfully"),
	CONTACT_US_SAVED("contact.us.saved"),
	USER_DELETED("user.deleted"),
	LOGOUT_SUCCESS_MESSAGE("logout.success.message"),
	BOOKEDCALL_SUCCESS_MESSAGE("bookedcall.success.message"), 
	BOOKEDCALL_UPDATE_MESSAGE("bookedcall.update.message"), 
	ACCOUNT_SWITCHED_SUCCESSFULLY("account.switch.successfully"),
	CATEGORY_ADDED_SUCCESSFULLY("category.added.successfully"),
	CATEGORY_UPDATED_SUCCESSFULLY("category.updated.successfully"),
	CATEGORY_DELETED_SUCCESSFULLY("category.deleted.successfully"),
	CATEGORY_ACTIVE_SUCCESSFULLY("category.active.successfully"),
	CATEGORY_INACTIVE_SUCCESSFULLY("category.inactive.successfully"),
	SUB_CATEGORY_ADDED_SUCCESSFULLY("sub.category.added.successfully"),
	SUB_CATEGORY_UPDATED_SUCCESSFULLY("sub.category.updated.successfully"),
	SUB_CATEGORY_DELETED_SUCCESSFULLY("sub.category.deleted.successfully"),
	SUB_CATEGORY_ACTIVE_SUCCESSFULLY("sub.category.active.successfully"),
	SUB_CATEGORY_INACTIVE_SUCCESSFULLY("sub.category.inactive.successfully"),
	SUB_ADMIN_ADDED_SUCCESSFULLY("sub.admin.added.successfully"),
	SUB_ADMIN_UPDATED_SUCCESSFULLY("sub.admin.updated.successfully"),
	SUB_ADMIN_DELETED_SUCCESSFULLY("sub.admin.deleted.successfully"),
	AUTHORITY_ALREADY_SET("authority.already.set"),
	AUTHORITY_UPDATED_SUCCESSFULLY("authority.updated.successfully"),
	AUTHORITY_DELETED_SUCCESSFULLY("authority.deleted.successfully"),
	CALLER_BANNED_SUCCESSFULLY("caller.banned.successfully"),
	CALLER_UN_BANNED_SUCCESSFULLY("caller.un.banned.successfully"),
	CALLER_DELETED_SUCCESSFULLY("caller.deleted.successfully"),
	LISTENER_BANNED_SUCCESSFULLY("listener.banned.successfully"),
	LISTENER_UN_BANNED_SUCCESSFULLY("listener.un.banned.successfully"),
	LISTENER_DELETED_SUCCESSFULLY("listener.deleted.successfully"),
	LISTENER_PROFILE_STATUS_UPDATED_SUCCESSFULLY("listener.profile.status.updated.successfully"),
	QUESTION_ADDED_SUCCESSFULLY("question.added.successfully"),
	QUESTION_UPDATED_SUCCESSFULLY("question.updated.successfully"),
	QUESTION_DELETED_SUCCESSFULLY("question.deleted.successfully"),
	REVIEWS_AND_RATINGS_DELETED_SUCCESSFULLY("reviews.and.ratings.deleted.successfully"),
	NOTIFICATION_SENT_SUCCESSFULLY("notification.sent.successfully"),
	NOTIFICATION_DELETED_SUCCESSFULLY("notification.deleted.successfully"),
	COMMISSION_RATE_UPDATED_SUCCESSFULLY("commission.rate.updated.successfully"),
	PAGE_CONTENT_UPDATED_SUCCESSFULLY("page.content.updated.successfully"),
	PAGE_CONTENT_ADDED_SUCCESSFULLY("page.content.added.successfully"),
	PAGE_CONTENT_DELETED_SUCCESSFULLY("page.content.deleted.successfully"),
	TRAINING_MATERIAL_ADDED_SUCCESSFULLY("training.material.added.successfully"),
	TRAINING_MATERIAL_UPDATED_SUCCESSFULLY("training.material.updated.successfully"),
	TRAINING_MATERIAL_DELETED_SUCCESSFULLY("training.material.deleted.successfully"),
	SUBSCRIPTION_PLAN_ADDED_SUCCESSFULLY("subscription.plan.added.successfully"),
	SUBSCRIPTION_PLAN_UPDATED_SUCCESSFULLY("subscription.plan.updated.successfully"),
	SUBSCRIPTION_PLAN_DELETED_SUCCESSFULLY("subscription.plan.deleted.successfully"),
	SUBSCRIPTION_PLAN_ACTIVE_SUCCESSFULLY("subscription.plan.active.successfully"),
	SUBSCRIPTION_PLAN_INACTIVE_SUCCESSFULLY("subscription.plan.inactive.successfully"),
	MODULE_ADDED_SUCCESSFULLY("module.added.successfully"),
	FAQ_ADDED_SUCCESSFULLY("faq.added.successfully"),
	FAQ_UPDATED_SUCCESSFULLY("faq.updated.successfully"),
	FAQ_DELETED_SUCCESSFULLY("faq.deleted.successfully"),
	CONTACT_US_DELETED_SUCCESSFULLY("contact.us.deleted.successfully"),
	RESPONSE_SENT_SUCCESSFULLY("response.sent.successfully"),
	BOOKED_CALL_DELETED_SUCCESSFULLY("booked.call.deleted.successfully"),
	NEWS_LETTER_SUBSCRIBED_SUCCESSFULLY("news.letter.subscribed.successfully"),
	COUPON_APPLIED_SUCCESSFULLY("coupon.applied.successfully"),
	BLOCKED_SUCCESSFULLY("blocked.successfully"),
	REPORTED_SUCCESSFULLY("reported.successfully"),
	PLAN_ALREADY_SUBSCRIBED("plan.already.subscribed"),
	NOTES_ADDED_SUCCESSFULLY("notes.added.successfully"),
	TWO_STEP_VERIFICATION_ENABLE("two.step.verification.enable"),
	TWO_STEP_VERIFICATION_DISABLE("two.step.verification.disable"),
	FORM_UPLOADED_SUCCESSFULLY("form.uploaded.successfully"),
	ANALYTICS_UPDATED_SUCCESSFULLY("analytics.updated.successfully"),
	COMMENT_UPDATED_SUCCESSFULLY("comment.updated.successfully"),
	PLAN_ADDED_SUCCESSFULLY("plan.added.successfully"),
	DATE_LIST_SUCCESSFULLY("date.list.successfully");

	
	String code;

	private SuccessMsgEnum(final String code) {
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

}
