package com.wiilisten.enums;

import lombok.Getter;

@Getter
public enum ErrorDataEnum {

    BAD_CREDENTIALS_MSG("bad.credentials.message"),
    NO_DATA_FOUND("no.data.found"), 
    EMAIL_EXIST("email.exist"),
    EMAIL_NOT_EXIST("email.not.exist"),
//    EMPTY_FIELD("empty.field"),
    INVALID_OTP("invalid.otp"),
//    NAME_EMPTY_FIELD("name.empty.field"),
//    PASSWORD_EMPTY_FIELD("password.empty.field"),
//    ROLE_EMPTY_FIELD("role.empty"),
//    EMAIL_EMPTY_FIELD("email.empty.field"),
    INVALID_USER("invalid.user"), 
    USERNAME_EXISTS("username.exit"),
    OTP_EXPIRED("otp.expired"), 
    EMAIL_OTP_EMPTY("email.otp.empty"),
    
    INVALID_REFERRAL_CODE("invalid.referral.code"),
    INVALID_OLD_PASSWORD("invalid.old.password"),
    NEW_OLD_PASSWORD_SAME("new.old.password.same"),
    CONTACT_EXIST("contact.exists"),
	LISTENER_NOT_FOUND("listener.not.found"),
	NO_PENDING_REQUEST_FOUND("pendingrequest.not.found"),
	NO_TIME_SLOT_AVAILABLE_FOR_THIS_DAY("timeslot.not.available.for.this.day"),
	CATEGORY_NOT_EXIST("category.not.exist"),
	SUB_CATEGORY_NOT_EXIST("sub.category.not.exist"),
	ADMIN_NOT_EXIST("admin.not.exist"),
	SUB_ADMIN_NOT_EXIST("sub.admin.not.exist"),
	UNAUTHORIZE_SUB_ADMIN("unauthorize.sub.admin"),
	CALLER_NOT_EXIST("caller.not.exist"),
	YOU_ARE_BANNED("you.are.banned"),
	LISTENER_NOT_EXIST("listener.not.exist"),
	QUESTION_NOT_EXIST("question.not.exist"),
	NO_CALLS_FOUND("no.calls.found"),
	REVIEWS_AND_RATINGS_NOT_EXIST("reviews.and.ratings.not.exist"),
	COMMISSION_RATE_NOT_EXIST("commission.rate.not.exist"),
	PAGE_CONTENT_EXIST("page.content.exist"),
	TRAINING_MATERIAL_NOT_EXIST("training.material.not.exist"),
	SUBSCRIPTION_PLAN_NOT_EXIST("subscription.plan.not.exist"),
	MODULE_NOT_FOUND("module.not.found"),
	NOTIFICATION_NOT_FOUND("notification.not.found"),
	FAQ_NOT_FOUND("faq.not.found"),
	ALREADY_SUBSCRIBED("already.subscribed"),
	INVALID_COUPON_CODE("invalid.coupon.code"),
	NO_EARNING_FOUND("no.earning.found"),
	NOT_PREMIUM_USER("not.premium.user"),
	PREVIOUS_PAYMENT_PENDING("previous.payment.pending"),
	PAYMENT_UNSUCCESSFULL("payment.unsuccessfull"),
	NO_ACTIVE_LISTENER_FOUND("no.active.listener.found"),
	INVALID_BANK_DETAILS("invalid.bank.details"),
	BANK_DETAILS_NOT_EXIST("bank.details.not.exist"),
    STRIPE_API_ERROR("something.went.wrong.in.stripe"),
    NO_PENDING_PAYMENT("no.pending.payment"),
    STRIPE_ACCOUNT_NOT_FOUND("stripe.account.not.found"),
    INSUFFICIENT_BALANCE("insufficient.balance"),
	PAYMENT_FAILED("payment.failed"),;

    private String code;

    private ErrorDataEnum(final String code) {
        this.code = code;
    }

}
