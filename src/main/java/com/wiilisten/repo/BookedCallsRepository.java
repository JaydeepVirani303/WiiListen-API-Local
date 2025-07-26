package com.wiilisten.repo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.ListenerProfile;

@Repository
public interface BookedCallsRepository extends BaseRepository<BookedCalls, Long> {

	@Query("from BookedCalls s where DATE(s.bookingDateTime) = :bookingTime and s.listener = :listener and s.active= true")
	List<BookedCalls> findByBookingDateTimeAndListenerAndActiveTrue(LocalDate bookingTime, ListenerProfile listener);

	Page<BookedCalls> findByCallerAndCallRequestStatusInAndActiveTrueOrderByCreatedAtDesc(CallerProfile caller,
			List<String> requestStatus, Pageable pageable);

	Page<BookedCalls> findByCallerAndCallRequestStatusAndCallStatusAndActiveTrueOrderByCreatedAtDesc(
			CallerProfile caller, String callRequestStatus, String callStatus, Pageable pageable);

	Page<BookedCalls> findByListenerAndCallRequestStatusAndActiveTrueOrderByCreatedAtDesc(ListenerProfile listener,
			String requestStatus, Pageable pageable);

	Page<BookedCalls> findByListenerAndCallRequestStatusAndCallStatusAndActiveTrueOrderByCreatedAtDesc(
			ListenerProfile listener, String callRequestStatus, String callStatus, Pageable pageable);

	List<BookedCalls> findTop10ByCallerAndCallRequestStatusAndCallStatusAndActiveTrueOrderByCreatedAtDesc(
			CallerProfile caller, String accepted, String scheduled);

	List<BookedCalls> findTop10ByListenerAndCallRequestStatusAndCallStatusAndActiveTrueOrderByCreatedAtDesc(
			ListenerProfile listener, String callRequestStatus, String callStatus);

	List<BookedCalls> findTop10ByCallerAndCallRequestStatusInAndActiveTrueOrderByCreatedAtDesc(CallerProfile caller,
			List<String> statuses);

	List<BookedCalls> findTop10ByListenerAndCallRequestStatusInAndActiveTrueOrderByCreatedAtDesc(
			ListenerProfile listener, List<String> requestStatus);

	Page<BookedCalls> findByCallerAndActiveTrue(CallerProfile caller, Pageable pageable);

	@Query("from BookedCalls s where DATE(s.bookingDateTime) = :bookingTime and s.caller = :caller")
	Page<BookedCalls> findByCallerAndBookingDate(CallerProfile caller, LocalDate bookingTime, Pageable pageable);

//	@Query(value = "SELECT s.* FROM booked_calls s " +
//            "WHERE s.booking_date_time BETWEEN DATE_ADD(UTC_TIMESTAMP(), INTERVAL 1 DAY) - INTERVAL 30 SECOND " +
//            "AND DATE_ADD(UTC_TIMESTAMP(), INTERVAL 1 DAY) + INTERVAL 30 SECOND " +
//            "AND s.active = 1", nativeQuery = true)
	@Query(value = "SELECT s.* FROM booked_calls s " +
            "WHERE TIMESTAMPDIFF(SECOND, UTC_TIMESTAMP(), s.booking_date_time) BETWEEN 86370 AND 86430 " +
            "AND s.active = 1", nativeQuery = true)
	List<BookedCalls> findCallsStartingInOneDay();

	@Query(value = "SELECT s.* FROM booked_calls s " +
            "WHERE TIMESTAMPDIFF(MINUTE, UTC_TIMESTAMP(), s.booking_date_time) = 60 " +
            "AND s.active = 1", nativeQuery = true)
	List<BookedCalls> findCallsStartingInOneHour();

	@Query(value = "SELECT s.* FROM booked_calls s " +
            "WHERE TIMESTAMPDIFF(MINUTE, UTC_TIMESTAMP(), s.booking_date_time) = 30 " +
            "AND s.active = 1", nativeQuery = true)
	List<BookedCalls> findCallsStartingInhalfHour();

	List<BookedCalls> findByBookingDateTimeBetweenAndActiveTrueOrderByIdDesc(LocalDateTime startDate,
			LocalDateTime endDate);

	List<BookedCalls> findAllByOrderByIdDesc();

	List<BookedCalls> findByActiveTrueOrderByIdDesc();

	List<BookedCalls> findByTypeAndActiveTrueOrderByIdDesc(String type);

	BookedCalls findByIdAndActiveTrue(Long id);

	List<BookedCalls> findByCallRequestStatusAndCallStatusAndPaymentStatusAndActiveTrueOrderByCreatedAtDesc(
			String callRequestStatus, String callStatus, String paymentStatus);

	Page<BookedCalls> findByPaymentStatusAndActiveTrueAndCallerOrderByCreatedAtDesc(String paymentStatus,
			CallerProfile caller, Pageable page);

	List<BookedCalls> findByBookingDateTimeBetweenAndTypeAndActiveTrueOrderByIdDesc(LocalDateTime startDate,
			LocalDateTime endDate, String type);

	List<BookedCalls> findByTypeAndActiveTrue(String type);

	List<BookedCalls> findByActiveTrue();

	BookedCalls findFirstByOrderByIdDesc();
	
	BookedCalls findByPaymentStatusAndCallStatusAndCallerIdAndActiveTrue(String paymentStatus,String callStatus,Long id);
	
	List<BookedCalls> findByBookingDateTimeBetweenAndActiveTrueAndCallerOrderByIdDesc(LocalDateTime startDate,
			LocalDateTime endDate,CallerProfile caller);
	
	List<BookedCalls> findByCallerAndListenerAndActiveTrueOrderByIdDesc(CallerProfile caller,ListenerProfile listener);
	
	// Finds calls starting in exactly 30 minutes
    @Query(value = "SELECT s.* FROM booked_calls s " +
            "WHERE s.booking_date_time = DATE_ADD(UTC_TIMESTAMP(), INTERVAL 30 MINUTE) " +
            "AND s.active = 1", nativeQuery = true)
    List<BookedCalls> findCallsStartingInExactlyHalfHour();

    // Finds calls starting in exactly 1 hour
    @Query(value = "SELECT s.* FROM booked_calls s " +
            "WHERE s.booking_date_time = DATE_ADD(UTC_TIMESTAMP(), INTERVAL 1 HOUR) " +
            "AND s.active = 1", nativeQuery = true)
    List<BookedCalls> findCallsStartingInExactlyOneHour();

    // Finds calls starting in exactly 1 day
    @Query(value = "SELECT s.* FROM booked_calls s " +
            "WHERE s.booking_date_time = DATE_ADD(UTC_TIMESTAMP(), INTERVAL 1 DAY) " +
            "AND s.active = 1", nativeQuery = true)
    List<BookedCalls> findCallsStartingInExactlyOneDay();

    // @Query("from BookedCalls s where s.listener = :listener and s.active= true and s.sponsored= true")
	Page<BookedCalls> findByListenerAndActiveTrueAndSponsoredTrue(ListenerProfile listener, Pageable pageable);

	@Query("SELECT bc FROM BookedCalls bc " +
			"JOIN bc.listener l " +
			"JOIN l.user u " +
			"WHERE FUNCTION('DATE', bc.bookingDateTime) = :bookingTime " +
			"AND bc.caller = :caller " +
			"AND LOWER(u.callName) LIKE LOWER(CONCAT('%', :callName, '%'))")
	Page<BookedCalls> findByCallerAndBookingDateAndCallNameJPQL(
			@Param("caller") CallerProfile caller,
			@Param("bookingTime") LocalDate bookingTime,
			@Param("callName") String callName,
			Pageable pageable);

	@Query("SELECT bc FROM BookedCalls bc " +
			"JOIN bc.listener l " +
			"JOIN l.user u " +
			"WHERE bc.caller = :caller " +
			"AND bc.active = true " +
			"AND LOWER(u.callName) LIKE LOWER(CONCAT('%', :callName, '%'))")
	Page<BookedCalls> findByCallerIdAndListenerCallNameAndActiveTrueJPQL(
			@Param("caller") CallerProfile caller,
			@Param("callName") String callName,
			Pageable pageable);

}
