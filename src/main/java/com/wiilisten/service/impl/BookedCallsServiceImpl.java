package com.wiilisten.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.service.BookedCallsService;

import jakarta.annotation.PostConstruct;

@Service
public class BookedCallsServiceImpl extends BaseServiceImpl<BookedCalls, Long> implements BookedCallsService {

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getBookedCallsRepository();
	}

	@Override
	public Page<BookedCalls> findByCallerProfileAndCallRequestStatusAndActiveTrue(CallerProfile caller,
			List<String> requestStatus, Pageable pageable) {
		return getDaoFactory().getBookedCallsRepository()
				.findByCallerAndCallRequestStatusInAndActiveTrueOrderByCreatedAtDesc(caller, requestStatus, pageable);
	}

	@Override
	public List<BookedCalls> findByBookingDateTimeAndListenerAndActiveTrue(LocalDate bookingTime,
			ListenerProfile listener) {
		// TODO Auto-generated method stub
		return getDaoFactory().getBookedCallsRepository().findByBookingDateTimeAndListenerAndActiveTrue(bookingTime,
				listener);
	}

	@Override
	public Page<BookedCalls> findByCallerProfileAndCallRequestStatusAndCallStatusAndActiveTrue(CallerProfile caller,
			String callRequestStatus, String callStatus, Pageable pageable) {
		// TODO Auto-generated method stub
		return getDaoFactory().getBookedCallsRepository()
				.findByCallerAndCallRequestStatusAndCallStatusAndActiveTrueOrderByCreatedAtDesc(caller,
						callRequestStatus, callStatus, pageable);
	}

	@Override
	public Page<BookedCalls> findByListenerProfileAndCallRequestStatusAndActiveTrue(ListenerProfile listener,
			String requestStatus, Pageable pageable) {
		// TODO Auto-generated method stub
		return getDaoFactory().getBookedCallsRepository()
				.findByListenerAndCallRequestStatusAndActiveTrueOrderByCreatedAtDesc(listener, requestStatus, pageable);
	}

	@Override
	public Page<BookedCalls> findByListenerProfileAndCallRequestStatusAndCallStatusAndActiveTrue(
			ListenerProfile listener, String callRequestStatus, String callStatus, Pageable pageable) {
		// TODO Auto-generated method stub
		return getDaoFactory().getBookedCallsRepository()
				.findByListenerAndCallRequestStatusAndCallStatusAndActiveTrueOrderByCreatedAtDesc(listener,
						callRequestStatus, callStatus, pageable);

	}

	@Override
	public List<BookedCalls> findTop10ByCallerProfileAndCallRequestStatusAndCallStatusAndActiveTrue(
			CallerProfile caller, String accepted, String scheduled) {
		// TODO Auto-generated method stub
		return getDaoFactory().getBookedCallsRepository()
				.findTop10ByCallerAndCallRequestStatusAndCallStatusAndActiveTrueOrderByCreatedAtDesc(caller, accepted,
						scheduled);

	}

	@Override
	public List<BookedCalls> findTop10ByListenerProfileAndCallRequestStatusAndCallStatusAndActiveTrue(
			ListenerProfile listener, String accepted, String scheduled) {
		// TODO Auto-generated method stub
		return getDaoFactory().getBookedCallsRepository()
				.findTop10ByListenerAndCallRequestStatusAndCallStatusAndActiveTrueOrderByCreatedAtDesc(listener,
						accepted, scheduled);
	}

	@Override
	public List<BookedCalls> findTop10ByCallerProfileAndCallRequestStatusAndActiveTrue(CallerProfile caller,
			List<String> statuses) {
		// TODO Auto-generated method stub
		return getDaoFactory().getBookedCallsRepository()
				.findTop10ByCallerAndCallRequestStatusInAndActiveTrueOrderByCreatedAtDesc(caller, statuses);
	}

	@Override
	public List<BookedCalls> findTop10ByListenerProfileAndCallRequestStatusAndActiveTrue(ListenerProfile listener,
			List<String> listStatus) {
		// TODO Auto-generated method stub
		return getDaoFactory().getBookedCallsRepository()
				.findTop10ByListenerAndCallRequestStatusInAndActiveTrueOrderByCreatedAtDesc(listener, listStatus);
	}

	@Override
	public Page<BookedCalls> findByCallerProfileAndActiveTrue(CallerProfile caller, Pageable pageable) {
		// TODO Auto-generated method stub
		return getDaoFactory().getBookedCallsRepository().findByCallerAndActiveTrue(caller, pageable);
	}

	@Override
	public Page<BookedCalls> findByCallerAndBookingDate(CallerProfile caller, LocalDate bookingDate,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return getDaoFactory().getBookedCallsRepository().findByCallerAndBookingDate(caller, bookingDate, pageable);
	}

	@Override
	public List<BookedCalls> findCallsStartingInOneDay() {
		// TODO Auto-generated method stub
		return getDaoFactory().getBookedCallsRepository().findCallsStartingInOneDay();
	}

	@Override
	public List<BookedCalls> findCallsStartingInOneHour() {
		// TODO Auto-generated method stub
		return getDaoFactory().getBookedCallsRepository().findCallsStartingInOneHour();
	}

	@Override
	public List<BookedCalls> findCallsStartingInHalfHour() {
		// TODO Auto-generated method stub
		return getDaoFactory().getBookedCallsRepository().findCallsStartingInhalfHour();
	}

	@Override
	public List<BookedCalls> findByBookingDateTimeBetweenAndActiveTrueOrderByIdDesc(LocalDate startDate,
			LocalDate endDate) {
		// TODO Auto-generated method stub
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		return getDaoFactory().getBookedCallsRepository()
				.findByBookingDateTimeBetweenAndActiveTrueOrderByIdDesc(startDateTime, endDateTime);
	}

	@Override
	public List<BookedCalls> findAllByOrderByIdDesc() {
		return getDaoFactory().getBookedCallsRepository().findAllByOrderByIdDesc();
	}

	@Override
	public List<BookedCalls> findByTypeAndActiveTrueOrderByIdDesc(String type) {
		return getDaoFactory().getBookedCallsRepository().findByTypeAndActiveTrueOrderByIdDesc(type);
	}

	@Override
	public List<BookedCalls> findByActiveTrueOrderByIdDesc() {
		return getDaoFactory().getBookedCallsRepository().findByActiveTrueOrderByIdDesc();
	}

	@Override
	public BookedCalls findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getBookedCallsRepository().findByIdAndActiveTrue(id);
	}

	@Override
	public List<BookedCalls> findByCallRequestStatusAndCallStatusAndPaymentStatusAndActiveTrueOrderByCreatedAtDesc(
			String callRequestStatus, String callStatus, String paymentStatus) {
		return getDaoFactory().getBookedCallsRepository()
				.findByCallRequestStatusAndCallStatusAndPaymentStatusAndActiveTrueOrderByCreatedAtDesc(
						callRequestStatus, callStatus, paymentStatus);
	}

	@Override
	public Page<BookedCalls> findByPaymentStatusAndActiveTrueAndCallerOrderByCreatedAtDesc(String paymentStatus,
			CallerProfile caller, Pageable page) {
		return getDaoFactory().getBookedCallsRepository()
				.findByPaymentStatusAndActiveTrueAndCallerOrderByCreatedAtDesc(paymentStatus, caller, page);
	}

	@Override
	public List<BookedCalls> findByBookingDateTimeBetweenAndTypeAndActiveTrueOrderByIdDesc(LocalDate startDate,
			LocalDate endDate, String type) {
		LocalDateTime startDateTime = startDate.atStartOfDay();
		LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
		return getDaoFactory().getBookedCallsRepository()
				.findByBookingDateTimeBetweenAndTypeAndActiveTrueOrderByIdDesc(startDateTime, endDateTime, type);
	}

	@Override
	public List<BookedCalls> findByTypeAndActiveTrue(String type) {
		return getDaoFactory().getBookedCallsRepository().findByTypeAndActiveTrue(type);
	}

	@Override
	public List<BookedCalls> findByActiveTrue() {
		return getDaoFactory().getBookedCallsRepository().findByActiveTrue();
	}

	@Override
	public BookedCalls findFirstByOrderByIdDesc() {
		return getDaoFactory().getBookedCallsRepository().findFirstByOrderByIdDesc();
	}

	@Override
	public BookedCalls findByPaymentStatusAndCallStatusAndCallerIdAndActiveTrue(String paymentStatus, String callStatus,
			Long id) {
		return getDaoFactory().getBookedCallsRepository()
				.findByPaymentStatusAndCallStatusAndCallerIdAndActiveTrue(paymentStatus, callStatus, id);
	}

	@Override
	public List<BookedCalls> findByBookingDateTimeBetweenAndActiveTrueAndCallerOrderByIdDesc(LocalDateTime startDate,
			LocalDateTime endDate, CallerProfile caller) {
		return getDaoFactory().getBookedCallsRepository().findByBookingDateTimeBetweenAndActiveTrueAndCallerOrderByIdDesc(startDate, endDate, caller);
	}

	@Override
	public List<BookedCalls> findByCallerAndListenerAndActiveTrueOrderByIdDesc(CallerProfile caller,
			ListenerProfile listener) {
		return getDaoFactory().getBookedCallsRepository().findByCallerAndListenerAndActiveTrueOrderByIdDesc(caller, listener);
	}

	@Override
	public Page<BookedCalls> findByListenerProfileAndActiveTrueAndSponsoredTrue(ListenerProfile listener, Pageable pageable) {
		
		return getDaoFactory().getBookedCallsRepository().findByListenerAndActiveTrueAndSponsoredTrue(listener, pageable);
	}

	@Override
	public Page<BookedCalls> findByCallerAndBookingDateAndListenerUserCallName(CallerProfile caller, LocalDate bookingDate, String callName,
														Pageable pageable) {
		return getDaoFactory().getBookedCallsRepository().findByCallerAndBookingDateAndCallNameJPQL(caller, bookingDate, callName, pageable);
	}

	@Override
	public Page<BookedCalls> findByCallerIdAndListenerCallNameAndActiveTrue(CallerProfile caller, String callName,
																			   Pageable pageable) {
		return getDaoFactory().getBookedCallsRepository().findByCallerIdAndListenerCallNameAndActiveTrueJPQL(caller, callName, pageable);
	}
}
