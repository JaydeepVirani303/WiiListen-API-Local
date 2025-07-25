package com.wiilisten.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.ListenerProfile;

public interface BookedCallsService extends BaseService<BookedCalls, Long> {

	List<BookedCalls> findByBookingDateTimeAndListenerAndActiveTrue(LocalDate bookingTime, ListenerProfile listener);

	Page<BookedCalls> findByCallerProfileAndCallRequestStatusAndActiveTrue(CallerProfile caller,
			List<String> listStatus, Pageable pageable);

	Page<BookedCalls> findByCallerProfileAndCallRequestStatusAndCallStatusAndActiveTrue(CallerProfile caller,
			String callRequestStatus, String callStatus, Pageable pageable);

	Page<BookedCalls> findByListenerProfileAndCallRequestStatusAndActiveTrue(ListenerProfile listener,
			String requestStatus, Pageable pageable);

	Page<BookedCalls> findByListenerProfileAndCallRequestStatusAndCallStatusAndActiveTrue(ListenerProfile listener,
			String callRequestStatus, String callStatus, Pageable pageable);

	List<BookedCalls> findTop10ByCallerProfileAndCallRequestStatusAndCallStatusAndActiveTrue(CallerProfile caller,
			String accepted, String scheduled);

	List<BookedCalls> findTop10ByListenerProfileAndCallRequestStatusAndCallStatusAndActiveTrue(ListenerProfile listener,
			String accepted, String scheduled);

	List<BookedCalls> findTop10ByCallerProfileAndCallRequestStatusAndActiveTrue(CallerProfile caller,
			List<String> listStatus);

	List<BookedCalls> findTop10ByListenerProfileAndCallRequestStatusAndActiveTrue(ListenerProfile listener,
			List<String> listStatus);

	Page<BookedCalls> findByCallerProfileAndActiveTrue(CallerProfile caller, Pageable pageable);

	Page<BookedCalls> findByCallerAndBookingDate(CallerProfile caller, LocalDate bookingDate, Pageable pageable);

	List<BookedCalls> findCallsStartingInOneDay();

	List<BookedCalls> findCallsStartingInOneHour();

	List<BookedCalls> findCallsStartingInHalfHour();

	List<BookedCalls> findByBookingDateTimeBetweenAndActiveTrueOrderByIdDesc(LocalDate startDate, LocalDate endDate);

	List<BookedCalls> findAllByOrderByIdDesc();

	List<BookedCalls> findByActiveTrueOrderByIdDesc();

	List<BookedCalls> findByTypeAndActiveTrueOrderByIdDesc(String type);

	BookedCalls findByIdAndActiveTrue(Long id);

	List<BookedCalls> findByCallRequestStatusAndCallStatusAndPaymentStatusAndActiveTrueOrderByCreatedAtDesc(
			String callRequestStatus, String callStatus, String paymentStatus);

	Page<BookedCalls> findByPaymentStatusAndActiveTrueAndCallerOrderByCreatedAtDesc(String paymentStatus,
			CallerProfile caller, Pageable page);

	List<BookedCalls> findByBookingDateTimeBetweenAndTypeAndActiveTrueOrderByIdDesc(LocalDate startDate,
			LocalDate endDate, String type);

	List<BookedCalls> findByTypeAndActiveTrue(String type);

	List<BookedCalls> findByActiveTrue();

	BookedCalls findFirstByOrderByIdDesc();

	BookedCalls findByPaymentStatusAndCallStatusAndCallerIdAndActiveTrue(String paymentStatus, String callStatus,
			Long id);

	List<BookedCalls> findByBookingDateTimeBetweenAndActiveTrueAndCallerOrderByIdDesc(LocalDateTime startDate,
			LocalDateTime endDate, CallerProfile caller);

	List<BookedCalls> findByCallerAndListenerAndActiveTrueOrderByIdDesc(CallerProfile caller, ListenerProfile listener);

	Page<BookedCalls> findByListenerProfileAndActiveTrueAndSponsoredTrue(ListenerProfile listener, Pageable pageable);

	Page<BookedCalls> findByCallerAndBookingDateAndListenerUserCallName(CallerProfile caller, LocalDate bookingDate, String callName, Pageable pageable);

	Page<BookedCalls> findByCallerIdAndListenerCallNameAndActiveTrue(CallerProfile caller, String callName, Pageable pageable);
}
