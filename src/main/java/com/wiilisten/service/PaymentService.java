package com.wiilisten.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.model.Account;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.EphemeralKey;
import com.stripe.model.ExternalAccountCollection;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Payout;
import com.stripe.model.Token;
import com.stripe.model.Transfer;
import com.stripe.net.RequestOptions;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.EphemeralKeyCreateParams;
import com.stripe.param.PaymentIntentCaptureParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PayoutCreateParams;
import com.stripe.param.TransferCreateParams;
import com.wiilisten.entity.User;

@Service
public class PaymentService {
	@Value("${stripe.SecretKey}")
	private String StripeKey;

	private String customerId;

	public Customer createStripeCustomer(User user) {
		// TODO Auto-generated method stub
		Stripe.apiKey = StripeKey;
		try {
			CustomerCreateParams params = CustomerCreateParams.builder().setName(user.getCallName())
					.setEmail(user.getEmail()).build();
			Customer customer = Customer.create(params);
			System.out.println(customer.getId());
			return customer;

		} catch (StripeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public String getEphemeralKey(String customerId) {
		Stripe.apiKey = StripeKey;
		// Create a map of parameters for the Ephemeral Key
		Map<String, Object> params = new HashMap<>();
		params.put("customer", customerId);

		// Specify the API version
		EphemeralKeyCreateParams createParams = EphemeralKeyCreateParams.builder().putAllExtraParam(params)
				.setStripeVersion("2020-08-27") // Replace with the required API version
				.build();

		// Create the Ephemeral Key
		try {
			EphemeralKey key = EphemeralKey.create(createParams);

			return key.getSecret();
		} catch (StripeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public PaymentIntent createPaymentIntenet(double finalAmount, String customerId) {
		// Minimum allowed amount in cents (Stripe requires at least $0.50 = 50 cents)
		long MIN_AMOUNT = 50;

		long amountInCents = Math.round(finalAmount * 100);
		// Check and set default if below minimum
		if (amountInCents < MIN_AMOUNT) {
//			System.out.println("Amount too small. Setting default minimum amount of $0.50 USD.");
			amountInCents = MIN_AMOUNT;
		}
		Stripe.apiKey = StripeKey;
		PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
				.setAmount(amountInCents)
				.setCurrency("usd")
				.setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
				.addPaymentMethodType("card")
				.setCustomer(customerId);

		paramsBuilder.setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION);

		PaymentIntentCreateParams params = paramsBuilder.build();

		try {
			PaymentIntent paymentIntent = PaymentIntent.create(params);
			return paymentIntent;
		} catch (StripeException e) {
			e.printStackTrace();
		}
		return null;
	}

	public PaymentIntent createPaymentIntent(Long finalAmount, String customerId, boolean saveCard) {
		Stripe.apiKey = StripeKey;
		PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
				.setAmount(finalAmount)
				.setCurrency("usd")
				.addPaymentMethodType("card")
				.setCustomer(customerId);

		// If saveCard is true, set setup_future_usage to 'off_session'
		if (saveCard) {
			paramsBuilder.setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION);
		}

		PaymentIntentCreateParams params = paramsBuilder.build();

		try {
			PaymentIntent paymentIntent = PaymentIntent.create(params);
			return paymentIntent;
		} catch (StripeException e) {
			e.printStackTrace();
		}
		return null;
	}

	public PaymentIntent createSubscriptionPaymentIntenet(Long finalamount) {
		Stripe.apiKey = StripeKey;
		PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
				.setAmount(finalamount)
				.setCurrency("usd")
				.addPaymentMethodType("card")

				.build();

		try {
			PaymentIntent paymentIntent = PaymentIntent.create(params);
			return paymentIntent;
		} catch (StripeException e) {
			e.printStackTrace();
		}
		return null;
	}

	public PaymentIntent capturePaymentIntent(String paymentIntentId, double amount) throws StripeException {
		Stripe.apiKey = StripeKey;
		amount = amount * 100;
		// Minimum allowed amount in cents (Stripe requires at least $0.50 = 50 cents)
		long MIN_AMOUNT = 50;

		// Check and set default if below minimum
		if (amount < MIN_AMOUNT) {
			amount = MIN_AMOUNT;
		}
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

		PaymentIntentCaptureParams params = PaymentIntentCaptureParams.builder().setAmountToCapture((long) amount).build();

		try {
			PaymentIntent capturedPaymentIntent = paymentIntent.capture(params);
			return capturedPaymentIntent;
		} catch (StripeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public PaymentService() {
		Stripe.apiKey = StripeKey;
	}

	// public Map<String, Object> createConnectedAccount(Map<String, String>
	// sellerData) throws Exception {
	// try {
	// Stripe.apiKey = StripeKey;
	// // Define account creation parameters
	// AccountCreateParams.Builder accountParamsBuilder =
	// AccountCreateParams.builder()
	// .setType(AccountCreateParams.Type.CUSTOM)
	// .setCountry(sellerData.get("country"))
	// .setEmail(sellerData.get("email"))
	// .setCapabilities(AccountCreateParams.Capabilities.builder()
	// .setTransfers(AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build())
	// .build());

	// // Create a bank account token
	// String bankAccountToken = createBankAccountToken("000999999991",
	// "110000000");

	// // Create the connected account with external account details
	// AccountCreateParams accountParams = accountParamsBuilder
	// .setAccountToken(bankAccountToken)
	// .build();

	// Account account = Account.create(accountParams);
	// String accountId = account.getId();

	// // Extract external account ID
	// ExternalAccountCollection externalAccounts = account.getExternalAccounts();
	// String externalAccountId = externalAccounts != null &&
	// !externalAccounts.getData().isEmpty()
	// ? externalAccounts.getData().get(0).getId()
	// : null;

	// // Handle bank verification
	// if (externalAccountId != null) {
	// // Bank verification logic (if needed)
	// return Map.of(
	// "account_id", accountId,
	// "external_account_id", externalAccountId,
	// "verification_status", "verified");
	// } else {
	// return Map.of(
	// "account_id", accountId,
	// "external_account_id", "",
	// "verification_status", "no_external_account",
	// "error", "No external account found. Please add bank details to verify.");
	// }

	// } catch (StripeException e) {
	// throw new Exception("Stripe error: " + e.getMessage(), e);
	// }
	// }

	public Map<String, Object> createConnectedAccount(Map<String, String> sellerData) throws Exception {
		try {
			Stripe.apiKey = StripeKey;

			// Define account creation parameters
			AccountCreateParams.Builder accountParamsBuilder = AccountCreateParams.builder()
					.setType(AccountCreateParams.Type.CUSTOM)
					.setCountry(sellerData.get("country"))
					.setDocuments(null)
					.setBusinessType(AccountCreateParams.BusinessType.COMPANY)
					.setEmail(sellerData.get("email"))
					.setCapabilities(AccountCreateParams.Capabilities.builder()
						.setTransfers(
							AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build())
						.setCardPayments(
							AccountCreateParams.Capabilities.CardPayments.builder().setRequested(true).build())
						.build());

			String bankAccountToken = createBankAccountToken("000123456789", "110000000");

			System.err.println("bankAccountToken: " + bankAccountToken);

			AccountCreateParams accountParams = accountParamsBuilder
					.setExternalAccount(bankAccountToken)
					.build();

			Account account = Account.create(accountParams);
			String accountId = account.getId();

			ExternalAccountCollection externalAccounts = account.getExternalAccounts();
			String externalAccountId = externalAccounts != null && !externalAccounts.getData().isEmpty()
					? externalAccounts.getData().get(0).getId()
					: null;

			if (externalAccountId != null) {
				return Map.of(
						"account_id", accountId,
						"external_account_id", externalAccountId,
						"verification_status", "verified");
			} else {
				return Map.of(
						"account_id", accountId,
						"external_account_id", "",
						"verification_status", "no_external_account",
						"error", "No external account found. Please add bank details to verify.");
			}
		} catch (StripeException e) {
			throw new Exception("Stripe error: " + e.getMessage(), e);
		}
	}

	public Map<String, Object> createTransfer(Map<String, Object> accountData) throws Exception {
		try {

			System.err.println("accountData: " + accountData);

			Stripe.apiKey = StripeKey;
			Object accountObj = accountData.get("account");
			if (!(accountObj instanceof Map)) {
				throw new IllegalArgumentException("Invalid account data");
			}
			Map<?, ?> accountMap = (Map<?, ?>) accountObj;
			Map<String, String> account = new HashMap<>();
			for (Map.Entry<?, ?> entry : accountMap.entrySet()) {
				if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
					account.put((String) entry.getKey(), (String) entry.getValue());
				} else {
					throw new IllegalArgumentException("Invalid account data entry");
				}
			}
			String accountdata = account.get("account_id");
			if (accountdata == null || accountdata.isEmpty()) {
				accountdata = account.get("external_account_id");
			}

			Account accounts = Account.retrieve(accountdata);
			if (!"active".equals(accounts.getCapabilities().getTransfers())) {
				throw new IllegalArgumentException("The destination account does not have the required capabilities enabled.");
			}

			TransferCreateParams transferParams = TransferCreateParams.builder()
					.setAmount((long) 11 * 100) // Stripe uses cents
					.setCurrency("USD")
					.setDestination(accountdata)
					.build();

			Transfer transfer = Transfer.create(transferParams);

			return Map.of("code", "1", "data", transfer);
		} catch (StripeException e) {
			return Map.of("code", "0", "message", e.getMessage());
		} catch (IllegalArgumentException e) {
			return Map.of("code", "0", "message", e.getMessage());
		}
	}

	public Map<String, Object> createPayout(Map<String, Object> accountData) throws Exception {
		try {

			Stripe.apiKey = StripeKey;
			PayoutCreateParams payoutParams = PayoutCreateParams.builder()
					.setAmount((long) 11 * 100) // Stripe uses cents
					.setCurrency("USD")
					.setDestination((String) accountData.get("bankAccountId"))
					.build();

			RequestOptions requestOptions = RequestOptions.builder()
					.setStripeAccount((String) accountData.get("accountId"))
					.build();
			Payout payout = Payout.create(payoutParams, requestOptions);

			return Map.of("code", "1", "data", payout);
		} catch (StripeException e) {
			return Map.of("code", "0", "message", e.getMessage());
		}
	}

	// // Existing methods...
	//
	// /**
	// * Adds a bank account to a Stripe customer.
	// */
	// public BankAccount createBankAccount(String customerId, String accountNumber,
	// String routingNumber) {
	// try {
	// Map<String, Object> params = new HashMap<>();
	// params.put("source", createBankAccountToken(accountNumber, routingNumber));
	//
	// BankAccount bankAccount = (BankAccount)
	// Customer.retrieve(customerId).getSources().create(params);
	// return bankAccount;
	// } catch (StripeException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	/**
	 * Creates a bank account token.
	 */
	private String createBankAccountToken(String accountNumber, String routingNumber) {
		Map<String, Object> bankAccountParams = new HashMap<>();
		bankAccountParams.put("country", "US");
		bankAccountParams.put("currency", "usd");
		bankAccountParams.put("account_holder_name", "Test Holder");
		bankAccountParams.put("account_holder_type", "individual");
		bankAccountParams.put("routing_number", routingNumber);
		bankAccountParams.put("account_number", accountNumber);

		Map<String, Object> params = new HashMap<>();
		params.put("bank_account", bankAccountParams);

		try {
			Token token = Token.create(params);
			return token.getId();
		} catch (StripeException e) {
			e.printStackTrace();
		}
		return null;
	}
	//
	// /**
	// * Verifies a bank account.
	// */
	// public boolean verifyBankAccount(String customerId, String bankAccountId,
	// int[] amounts) {
	// try {
	// BankAccount bankAccount = (BankAccount)
	// Customer.retrieve(customerId).getSources().retrieve(bankAccountId);
	//
	// Map<String, Object> params = new HashMap<>();
	// params.put("amounts", amounts);
	//
	// bankAccount.verify(params);
	// return true;
	// } catch (StripeException e) {
	// e.printStackTrace();
	// }
	// return false;
	// }
	//
	// /**
	// * Creates a payment intent for ACH.
	// */
	// public PaymentIntent createAchPaymentIntent(Long amount, String customerId,
	// String paymentMethodId) {
	// PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
	// .setAmount(amount)
	// .setCurrency("usd")
	// .setCustomer(customerId)
	// .addPaymentMethodType("us_bank_account")
	// .setPaymentMethod(paymentMethodId)
	// .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
	// .build();
	//
	// try {
	// return PaymentIntent.create(params);
	// } catch (StripeException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	// //Modified capture payment for ACH
	// public PaymentIntent capturePaymentIntent(String paymentIntentId, Long
	// amount) throws StripeException {
	// Stripe.apiKey = StripeKey;
	//
	// PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
	//
	// PaymentIntentCaptureParams params = PaymentIntentCaptureParams.builder()
	// .setAmountToCapture(amount)
	// .build();
	//
	// try {
	// return paymentIntent.capture(params);
	// } catch (StripeException e) {
	// e.printStackTrace();
	// throw e;
	// }
	// }

}