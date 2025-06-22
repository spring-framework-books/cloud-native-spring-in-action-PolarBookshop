package com.polarbookshop.dispatcherservice;

//DTO containing the order identifier as a Long field
public record OrderDispatchedMessage (
		Long orderId
){}