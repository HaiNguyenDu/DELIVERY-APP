package com.example.grabapp.respone

data class GoongDirectionApiResponse(
    val code: String,
    val message: String?,
    val geocoded_waypoints: List<GeocodedWaypoint>?,
    val routes: List<Route>?
)

data class GeocodedWaypoint(
    val geocoder_status: String,
    val place_id: String
)

data class Route(
    val bounds: Bounds?,
    val legs: List<Leg>,
    val overview_polyline: Polyline?,
    val summary: String?,
    val warnings: List<String>?,
    val waypoint_order: List<Int>?
)

data class Bounds(
    val northeast: LocationPoint? = null,
    val southwest: LocationPoint? = null
)

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val end_address: String,
    val end_location: LocationPoint,
    val start_address: String,
    val start_location: LocationPoint,
    val steps: List<Step>
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)

data class LocationPoint(
    val lat: Double,
    val lng: Double
)

data class Step(
    val distance: Distance,
    val duration: Duration,
    val end_location: LocationPoint,
    val html_instructions: String,
    val maneuver: String?,
    val polyline: Polyline,
    val start_location: LocationPoint,
    val travel_mode: String
)

data class Polyline(
    val points: String
)
