
CREATE SCHEMA {schema};


SET default_tablespace = '';

SET default_with_oids = false;

CREATE SEQUENCE {schema}.hibernate_sequence START 1;
--
-- Name: imsaccommodationallocation; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsaccommodationallocation (
    allocation_date date NOT NULL,
    allocation_id integer NOT NULL,
    hotel_id character varying(20) NOT NULL,
    allocation integer
);


--
-- Name: imsaccommodationallocationsummary; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsaccommodationallocationsummary (
    allocation_id integer NOT NULL,
    hotel_id character varying(20) NOT NULL,
    allocation_description character varying(200),
    handback_days integer
);


--
-- Name: imsaccommodationboard; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsaccommodationboard (
    id bigint NOT NULL,
    board_code character varying(10),
    board_description character varying(200),
    hotel_id character varying(20)
);


--
-- Name: imsaccommodationcategory; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsaccommodationcategory (
    category character varying(50) NOT NULL
);


--
-- Name: imsaccommodationcnxpolicy; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsaccommodationcnxpolicy (
    id bigint NOT NULL,
    before_checkin_after_booking character varying(255),
    hotel_id character varying(20),
    line_id integer,
    number_of_days integer,
    penalty numeric(19,2),
    penalty_type character varying(255),
    policy_id integer,
    policy_name character varying(100),
    booking_conditions character varying(100)
);


--
-- Name: imsaccommodationfacility; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsaccommodationfacility (
    facility character varying(50) NOT NULL
);


--
-- Name: imsaccommodationrate; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsaccommodationrate (
    id bigint NOT NULL,
    allocation_id integer,
    board_code character varying(10),
    friday boolean,
    monday boolean,
    saturday boolean,
    sunday boolean,
    thursday boolean,
    tuesday boolean,
    wednesday boolean,
    description character varying(1000),
    hotel_id character varying(20),
    nett numeric(19,2),
    number_of_adults integer,
    number_of_children integer,
    paxmix_pricing boolean,
    policy_id integer,
    rate_id integer,
    roomtype_id integer,
    rrp numeric(19,2),
    season_id integer,
    bundles_only boolean,
    minimum_nights integer,
    rate_group character varying(20),
    per_infant_surcharge numeric(19,2)
);


--
-- Name: imsaccommodationrc; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsaccommodationrc (
    hotel_id character varying(100) NOT NULL,
    accommodation_name character varying(1000),
    city character varying(100),
    country_code character varying(2),
    full_form_address character varying(1000),
    geo_accuracy numeric(19,2),
    postcode character varying(100),
    state character varying(100),
    street character varying(100),
    chain character varying(100),
    checkin_time character varying(20),
    checkout_time character varying(20),
    currency character varying(3),
    description text,
    email character varying(100),
    errata_json text,
    facility_groups_json text,
    image_thumbnail text,
    images_json text,
    introduction text,
    last_update date,
    olery_company_code bigint,
    phone character varying(100),
    product_type character varying(255),
    rating numeric(19,2),
    category character varying(100),
    country_name character varying(50),
    latitude numeric(8,5),
    longitude numeric(8,5),
    last_updated timestamp without time zone,
    supplier character varying(100),
    rrp_currency character varying(3),
    child_age bigint,
    infant_age bigint
);


--
-- Name: imsaccommodationroomtype; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsaccommodationroomtype (
    id bigint NOT NULL,
    bedding_description character varying(200),
    description character varying(1000),
    hotel_id character varying(20),
    maximum_adults integer,
    maximum_people integer,
    room_size character varying(200),
    roomtype_id integer,
    simple_allocation boolean
);


--
-- Name: imsaccommodationsale; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsaccommodationsale (
    id integer NOT NULL,
    accommodation_name character varying(1000),
    board character varying(100),
    booking_date_time timestamp without time zone,
    checkin date,
    checkout date,
    cnx_policy character varying(250),
    country_code_of_origin character varying(2),
    currency character varying(3),
    given_name character varying(100),
    guest_information character varying(1000),
    hotel_id character varying(100),
    internal_booking_reference character varying(100),
    internal_item_reference character varying(100),
    item_status character varying(255),
    nett_price numeric(19,2),
    rate_name character varying(100),
    room_name character varying(1000),
    room_number integer,
    rrp_price numeric(19,2),
    surname character varying(100),
    telephone character varying(100),
    title character varying(20),
    rrp_currency character varying(3)
);


--
-- Name: imsaccommodationseason; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsaccommodationseason (
    id bigint NOT NULL,
    date_from date,
    date_to date,
    hotel_id character varying(20),
    season_id integer,
    season_name character varying(200)
);


--
-- Name: imsaccommodationspecial; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsaccommodationspecial (
    id bigint NOT NULL,
    adjust_percentage numeric(19,2),
    adjust_value numeric(19,2),
    book_from date,
    book_to date,
    checkin_from date,
    checkin_to date,
    days_in_advance_less integer,
    days_in_advance_more integer,
    free_nights integer,
    hotel_id character varying(20),
    minimum_stay integer,
    rate_id integer,
    special_id integer,
    description character varying(400),
    rate_ids character varying(100)
);


--
-- Name: imsactivity; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsactivity (
    id integer NOT NULL,
    additional_information_json text,
    categories_json character varying(500),
    departure_point character varying(500),
    duration character varying(500),
    exclusions_json text,
    external_activity_id character varying(20),
    geo_accuracy numeric(19,2),
    hotel_pickup boolean,
    images_json text,
    inclusions_json text,
    local_operator_information text,
    name character varying(200),
    overview text,
    schedule_and_pricing text,
    voucher_information text,
    activitysupplier_id integer NOT NULL,
    allotment_by_departure_and_option boolean,
    hotel_pickups_json character varying(500),
    operator character varying(100),
    last_updated timestamp without time zone,
    terms_and_conditions text,
    latitude numeric(8,5),
    longitude numeric(8,5),
    city character varying(100),
    country_code character varying(2),
    state character varying(100)
);


--
-- Name: imsactivityallotment; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsactivityallotment (
    activity_id integer NOT NULL,
    allotment_date date NOT NULL,
    departure_time_id integer NOT NULL,
    option_id integer NOT NULL,
    allotment integer
);


--
-- Name: imsactivitydeparturetime; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsactivitydeparturetime (
    id integer NOT NULL,
    deparure_time time without time zone,
    name character varying(200),
    activity_id integer NOT NULL,
    departure_time time without time zone,
    last_updated timestamp without time zone
);


--
-- Name: imsactivityoption; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsactivityoption (
    id integer NOT NULL,
    allotment_id integer,
    allow_infant_if_under integer,
    bundles_only boolean,
    currency character varying(3),
    days character varying(255),
    name character varying(200),
    nett_price numeric(19,2),
    rrp_price numeric(19,2),
    ticketing_description character varying(1000),
    activity_id integer NOT NULL,
    external_code character varying(200),
    price_blocks_json text,
    last_updated timestamp without time zone
);


--
-- Name: imsactivityoptionblock; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsactivityoptionblock (
    id integer NOT NULL,
    code character varying(200),
    name character varying(200),
    activity_id integer NOT NULL
);


--
-- Name: imsactivitysale; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsactivitysale (
    id integer NOT NULL,
    activity_date date,
    age_list character varying(255),
    booking_date_time timestamp without time zone,
    count integer,
    country_code_of_origin character varying(2),
    departure_time_id integer,
    departure_time_name character varying(200),
    given_name character varying(100),
    internal_booking_reference character varying(100),
    internal_item_reference character varying(100),
    item_status character varying(255),
    name character varying(200),
    nett_currency character varying(3),
    nett_price numeric(19,2),
    option_id integer,
    option_name character varying(200),
    rrp_currency character varying(3),
    rrp_price numeric(19,2),
    surname character varying(100),
    telephone character varying(100),
    title character varying(20),
    activity_id integer NOT NULL,
    currency character varying(3),
    booking_question_answers text
);


--
-- Name: imsactivitysupplier; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsactivitysupplier (
    id integer NOT NULL,
    age_bands character varying(1000),
    default_margin numeric(19,2),
    external_supplier_id character varying(20),
    name character varying(200),
    show_supplier_name boolean,
    last_updated timestamp without time zone
);


--
-- Name: imsactivitysupplierageband; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsactivitysupplierageband (
    id integer NOT NULL,
    activity_supplier_id integer NOT NULL,
    ageband_id integer,
    band_name character varying(50),
    max_age numeric(19,2),
    min_age integer
);


--
-- Name: imscountry; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imscountry (
    countryid character varying(2) NOT NULL,
    country_name character varying(200)
);


--
-- Name: imsevent; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsevent (
    id integer NOT NULL,
    default_seatmap_image_url character varying(255),
    end_date date,
    external_event_id character varying(20),
    image_url character varying(255),
    name character varying(200),
    overview text,
    seat_map_not_available boolean,
    start_date date,
    start_time time without time zone,
    team_or_performer character varying(200),
    eventseries_id integer NOT NULL,
    eventsupplier_id integer NOT NULL,
    eventvenue_id integer NOT NULL,
    terms_and_conditions text,
    associated_external_merchandise_id character varying(20),
    last_updated timestamp without time zone,
    operator character varying(100)
);


--
-- Name: imseventallotment; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imseventallotment (
    id integer NOT NULL,
    allotment integer,
    maximum_sale integer,
    minimum_sale integer,
    multiple_pattern character varying(50),
    name character varying(200),
    on_request boolean,
    event_id integer NOT NULL,
    last_updated timestamp without time zone
);


--
-- Name: imseventclassification; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imseventclassification (
    id integer NOT NULL,
    allotment_id integer,
    bundles_only boolean,
    currency character varying(3),
    days character varying(255),
    name character varying(200),
    nett_price numeric(19,2),
    rrp_price numeric(19,2),
    ticketing_description character varying(1000),
    event_id integer NOT NULL,
    allow_infant_if_under integer,
    last_updated timestamp without time zone,
    rrp_currency character varying(3)
);


--
-- Name: imseventmerchandiselink; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imseventmerchandiselink (
    id integer NOT NULL,
    mandatory_inclusion boolean,
    eventseries_id integer NOT NULL,
    merchandise_id integer NOT NULL
);


--
-- Name: imseventsale; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imseventsale (
    id integer NOT NULL,
    allotment_id integer,
    allotment_name character varying(200),
    booking_date_time timestamp without time zone,
    classification_id integer,
    classification_name character varying(200),
    count integer,
    country_code_of_origin character varying(2),
    currency character varying(3),
    event_date date,
    given_name character varying(100),
    internal_booking_reference character varying(100),
    internal_item_reference character varying(100),
    item_status character varying(255),
    name character varying(200),
    nett_price numeric(19,2),
    rrp_price numeric(19,2),
    surname character varying(100),
    telephone character varying(100),
    ticketing_description character varying(1000),
    title character varying(20),
    event_id integer NOT NULL,
    rrp_currency character varying(3)
);


--
-- Name: imseventseries; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imseventseries (
    id integer NOT NULL,
    countries character varying(100),
    default_currency character varying(3),
    external_series_id character varying(20),
    image_url character varying(255),
    marketing_countries character varying(100),
    name character varying(200),
    overview text,
    eventtype_id integer NOT NULL,
    last_updated timestamp without time zone,
    active boolean,
    excluded_marketing_countries character varying(100)
);


--
-- Name: imseventsupplier; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imseventsupplier (
    id integer NOT NULL,
    default_margin numeric(19,2),
    external_supplier_id character varying(20),
    name character varying(200),
    show_supplier_name boolean,
    last_updated timestamp without time zone
);


--
-- Name: imseventtype; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imseventtype (
    id integer NOT NULL,
    name character varying(20)
);


--
-- Name: imseventvenue; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imseventvenue (
    id integer NOT NULL,
    city character varying(255),
    country_code character varying(255),
    country_name character varying(255),
    full_form_address character varying(255),
    postcode character varying(255),
    state character varying(255),
    street character varying(255),
    default_seatmap_image_url character varying(255),
    external_venue_id character varying(20),
    image_url character varying(255),
    name character varying(200),
    overview text,
    geo_accuracy numeric(19,2),
    last_updated timestamp without time zone,
    latitude numeric(8,5),
    longitude numeric(8,5)
);


--
-- Name: imslocation; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imslocation (
    id integer NOT NULL,
    location_name character varying(100),
    radius numeric(19,2),
    country_id character varying(2),
    geo_accuracy numeric(19,2),
    latitude numeric(8,5),
    longitude numeric(8,5)
);


--
-- Name: imsmerchandise; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsmerchandise (
    id integer NOT NULL,
    external_merchandise_id character varying(20),
    image_url character varying(255),
    name character varying(200),
    overview text,
    terms_and_conditions text,
    merchandisecategory_id integer NOT NULL,
    merchandisesupplier_id integer NOT NULL,
    images_json text,
    brands_json text,
    last_updated timestamp without time zone,
    bundles_only boolean,
    active boolean
);


--
-- Name: imsmerchandisecategory; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsmerchandisecategory (
    id integer NOT NULL,
    name character varying(20)
);


--
-- Name: imsmerchandiseoption; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsmerchandiseoption (
    id integer NOT NULL,
    allotment integer,
    currency character varying(3),
    name character varying(200),
    nett_price numeric(19,2),
    rrp_price numeric(19,2),
    merchandise_id integer NOT NULL,
    last_updated timestamp without time zone,
    rrp_currency character varying(3)
);


--
-- Name: imsmerchandisesale; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsmerchandisesale (
    id integer NOT NULL,
    booking_date_time timestamp without time zone,
    count integer,
    country_code_of_origin character varying(2),
    currency character varying(3),
    given_name character varying(100),
    internal_booking_reference character varying(100),
    internal_item_reference character varying(100),
    item_status character varying(255),
    name character varying(200),
    nett_price numeric(19,2),
    option_id integer,
    option_name character varying(200),
    rrp_price numeric(19,2),
    surname character varying(100),
    telephone character varying(100),
    title character varying(20),
    merchandise_id integer NOT NULL,
    rrp_currency character varying(3)
);


--
-- Name: imsmerchandisesupplier; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imsmerchandisesupplier (
    id integer NOT NULL,
    default_margin numeric(19,2),
    external_supplier_id character varying(20),
    name character varying(200),
    show_supplier_name boolean,
    last_updated timestamp without time zone
);


--
-- Name: imssupplier; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imssupplier (
    id bigint NOT NULL,
    accounts_email character varying(100),
    accounts_name character varying(100),
    accounts_phone character varying(20),
    contracting_email character varying(100),
    contracting_name character varying(100),
    contracting_phone character varying(20),
    customerservice_email character varying(100),
    customerservice_name character varying(100),
    customerservice_phone character varying(20),
    default_margin numeric(19,2),
    external_supplier_id character varying(40),
    for_accommodation boolean,
    for_activities boolean,
    for_events boolean,
    for_merchandise boolean,
    for_transportation boolean,
    gm_email character varying(100),
    gm_name character varying(100),
    gm_phone character varying(20),
    last_updated timestamp without time zone,
    reservations_email character varying(100),
    reservations_name character varying(100),
    reservations_phone character varying(20),
    show_supplier_name boolean,
    supplier_name character varying(100)
);


--
-- Name: imssystemproperties; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imssystemproperties (
    id integer NOT NULL,
    channel character varying(50),
    parameter character varying(50),
    site character varying(50),
    value character varying(255)
);


--
-- Name: imstransportationbasic; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imstransportationbasic (
    id bigint NOT NULL,
    currency character varying(3),
    friday boolean,
    monday boolean,
    saturday boolean,
    sunday boolean,
    thursday boolean,
    tuesday boolean,
    wednesday boolean,
    flight character varying(10),
    from_iata character varying(3),
    schedule_from date,
    schedule_to date,
    search_iata_from character varying(3),
    search_iata_to character varying(3),
    slug character varying(20),
    to_iata character varying(3),
    last_updated timestamp without time zone,
    requires_passport boolean,
    on_request boolean,
    supplier character varying(100),
    rrp_currency character varying(3),
    booking_conditions character varying(1000)
);


--
-- Name: imstransportationbasicclass; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imstransportationbasicclass (
    id bigint NOT NULL,
    adult_nett numeric(19,2),
    adult_rrp numeric(19,2),
    baggage_max_pieces integer,
    baggage_max_weight integer,
    child_nett numeric(19,2),
    child_rrp numeric(19,2),
    class_code character varying(1),
    class_description character varying(100),
    reference character varying(10),
    refundable boolean,
    transport_id bigint NOT NULL,
    last_updated timestamp without time zone
);


--
-- Name: imstransportationbasicsegment; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imstransportationbasicsegment (
    id bigint NOT NULL,
    arrival_airport_location_code character varying(3),
    arrival_day_extra integer,
    arrival_terminal character varying(100),
    arrival_time time without time zone,
    departure_airport_location_code character varying(3),
    departure_terminal character varying(100),
    departure_time time without time zone,
    flight_duration_minutes integer,
    marketing_airline_code character varying(3),
    marketing_airline_flight_number character varying(6),
    operating_airline_code character varying(3),
    operating_airline_flight_number character varying(6),
    segment_number integer,
    transport_id bigint NOT NULL,
    last_updated timestamp without time zone,
    passport_required boolean
);


--
-- Name: imstransportsale; Type: TABLE; Schema: {schema}; Owner: -
--

CREATE TABLE {schema}.imstransportsale (
    id integer NOT NULL,
    booking_date_time timestamp without time zone,
    country_code_of_origin character varying(2),
    currency character varying(3),
    given_name character varying(100),
    internal_booking_reference character varying(100),
    internal_item_reference character varying(100),
    item_status character varying(255),
    nett_price numeric(19,2),
    rrp_price numeric(19,2),
    surname character varying(100),
    telephone character varying(100),
    title character varying(20),
    transport_class character varying(10),
    transport_code character varying(100),
    transport_date date,
    transport_type character varying(10),
    traveller_information character varying(1000),
    rrp_currency character varying(3)
);



--
-- Name: imsaccommodationallocation imsaccommodationallocation_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsaccommodationallocation
    ADD CONSTRAINT imsaccommodationallocation_pkey PRIMARY KEY (allocation_date, allocation_id, hotel_id);


--
-- Name: imsaccommodationallocationsummary imsaccommodationallocationsummary_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsaccommodationallocationsummary
    ADD CONSTRAINT imsaccommodationallocationsummary_pkey PRIMARY KEY (allocation_id, hotel_id);


--
-- Name: imsaccommodationboard imsaccommodationboard_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsaccommodationboard
    ADD CONSTRAINT imsaccommodationboard_pkey PRIMARY KEY (id);


--
-- Name: imsaccommodationcategory imsaccommodationcategory_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsaccommodationcategory
    ADD CONSTRAINT imsaccommodationcategory_pkey PRIMARY KEY (category);


--
-- Name: imsaccommodationcnxpolicy imsaccommodationcnxpolicy_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsaccommodationcnxpolicy
    ADD CONSTRAINT imsaccommodationcnxpolicy_pkey PRIMARY KEY (id);


--
-- Name: imsaccommodationfacility imsaccommodationfacility_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsaccommodationfacility
    ADD CONSTRAINT imsaccommodationfacility_pkey PRIMARY KEY (facility);


--
-- Name: imsaccommodationrate imsaccommodationrate_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsaccommodationrate
    ADD CONSTRAINT imsaccommodationrate_pkey PRIMARY KEY (id);


--
-- Name: imsaccommodationrc imsaccommodationrc_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsaccommodationrc
    ADD CONSTRAINT imsaccommodationrc_pkey PRIMARY KEY (hotel_id);


--
-- Name: imsaccommodationroomtype imsaccommodationroomtype_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsaccommodationroomtype
    ADD CONSTRAINT imsaccommodationroomtype_pkey PRIMARY KEY (id);


--
-- Name: imsaccommodationsale imsaccommodationsale_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsaccommodationsale
    ADD CONSTRAINT imsaccommodationsale_pkey PRIMARY KEY (id);


--
-- Name: imsaccommodationseason imsaccommodationseason_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsaccommodationseason
    ADD CONSTRAINT imsaccommodationseason_pkey PRIMARY KEY (id);


--
-- Name: imsaccommodationspecial imsaccommodationspecial_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsaccommodationspecial
    ADD CONSTRAINT imsaccommodationspecial_pkey PRIMARY KEY (id);


--
-- Name: imsactivity imsactivity_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivity
    ADD CONSTRAINT imsactivity_pkey PRIMARY KEY (id);


--
-- Name: imsactivityallotment imsactivityallotment_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivityallotment
    ADD CONSTRAINT imsactivityallotment_pkey PRIMARY KEY (activity_id, allotment_date, departure_time_id, option_id);


--
-- Name: imsactivitydeparturetime imsactivitydeparturetime_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivitydeparturetime
    ADD CONSTRAINT imsactivitydeparturetime_pkey PRIMARY KEY (id);


--
-- Name: imsactivityoption imsactivityoption_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivityoption
    ADD CONSTRAINT imsactivityoption_pkey PRIMARY KEY (id);


--
-- Name: imsactivityoptionblock imsactivityoptionblock_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivityoptionblock
    ADD CONSTRAINT imsactivityoptionblock_pkey PRIMARY KEY (id);


--
-- Name: imsactivitysale imsactivitysale_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivitysale
    ADD CONSTRAINT imsactivitysale_pkey PRIMARY KEY (id);


--
-- Name: imsactivitysupplier imsactivitysupplier_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivitysupplier
    ADD CONSTRAINT imsactivitysupplier_pkey PRIMARY KEY (id);


--
-- Name: imsactivitysupplierageband imsactivitysupplierageband_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivitysupplierageband
    ADD CONSTRAINT imsactivitysupplierageband_pkey PRIMARY KEY (id);


--
-- Name: imscountry imscountry_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imscountry
    ADD CONSTRAINT imscountry_pkey PRIMARY KEY (countryid);


--
-- Name: imsevent imsevent_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsevent
    ADD CONSTRAINT imsevent_pkey PRIMARY KEY (id);


--
-- Name: imseventallotment imseventallotment_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventallotment
    ADD CONSTRAINT imseventallotment_pkey PRIMARY KEY (id);


--
-- Name: imseventclassification imseventclassification_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventclassification
    ADD CONSTRAINT imseventclassification_pkey PRIMARY KEY (id);


--
-- Name: imseventmerchandiselink imseventmerchandiselink_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventmerchandiselink
    ADD CONSTRAINT imseventmerchandiselink_pkey PRIMARY KEY (id);


--
-- Name: imseventsale imseventsale_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventsale
    ADD CONSTRAINT imseventsale_pkey PRIMARY KEY (id);


--
-- Name: imseventseries imseventseries_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventseries
    ADD CONSTRAINT imseventseries_pkey PRIMARY KEY (id);


--
-- Name: imseventsupplier imseventsupplier_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventsupplier
    ADD CONSTRAINT imseventsupplier_pkey PRIMARY KEY (id);


--
-- Name: imseventtype imseventtype_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventtype
    ADD CONSTRAINT imseventtype_pkey PRIMARY KEY (id);


--
-- Name: imseventvenue imseventvenue_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventvenue
    ADD CONSTRAINT imseventvenue_pkey PRIMARY KEY (id);


--
-- Name: imslocation imslocation_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imslocation
    ADD CONSTRAINT imslocation_pkey PRIMARY KEY (id);


--
-- Name: imsmerchandise imsmerchandise_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsmerchandise
    ADD CONSTRAINT imsmerchandise_pkey PRIMARY KEY (id);


--
-- Name: imsmerchandisecategory imsmerchandisecategory_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsmerchandisecategory
    ADD CONSTRAINT imsmerchandisecategory_pkey PRIMARY KEY (id);


--
-- Name: imsmerchandiseoption imsmerchandiseoption_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsmerchandiseoption
    ADD CONSTRAINT imsmerchandiseoption_pkey PRIMARY KEY (id);


--
-- Name: imsmerchandisesale imsmerchandisesale_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsmerchandisesale
    ADD CONSTRAINT imsmerchandisesale_pkey PRIMARY KEY (id);


--
-- Name: imsmerchandisesupplier imsmerchandisesupplier_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsmerchandisesupplier
    ADD CONSTRAINT imsmerchandisesupplier_pkey PRIMARY KEY (id);


--
-- Name: imssupplier imssupplier_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imssupplier
    ADD CONSTRAINT imssupplier_pkey PRIMARY KEY (id);


--
-- Name: imssystemproperties imssystemproperties_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imssystemproperties
    ADD CONSTRAINT imssystemproperties_pkey PRIMARY KEY (id);


--
-- Name: imstransportationbasic imstransportationbasic_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imstransportationbasic
    ADD CONSTRAINT imstransportationbasic_pkey PRIMARY KEY (id);


--
-- Name: imstransportationbasicclass imstransportationbasicclass_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imstransportationbasicclass
    ADD CONSTRAINT imstransportationbasicclass_pkey PRIMARY KEY (id);


--
-- Name: imstransportationbasicsegment imstransportationbasicsegment_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imstransportationbasicsegment
    ADD CONSTRAINT imstransportationbasicsegment_pkey PRIMARY KEY (id);


--
-- Name: imstransportsale imstransportsale_pkey; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imstransportsale
    ADD CONSTRAINT imstransportsale_pkey PRIMARY KEY (id);


--
-- Name: imstransportationbasic uk_6sahm2uhyvmjshrasior3ve2m; Type: CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imstransportationbasic
    ADD CONSTRAINT uk_6sahm2uhyvmjshrasior3ve2m UNIQUE (slug);


--
-- Name: imsaccommodationallocation_hotelid; Type: INDEX; Schema: {schema}; Owner: -
--

CREATE INDEX imsaccommodationallocation_hotelid ON {schema}.imsaccommodationallocation USING btree (hotel_id);


--
-- Name: imsaccommodationallocationsummary_hotelid; Type: INDEX; Schema: {schema}; Owner: -
--

CREATE INDEX imsaccommodationallocationsummary_hotelid ON {schema}.imsaccommodationallocationsummary USING btree (hotel_id);


--
-- Name: imsaccommodationboard_hotelid; Type: INDEX; Schema: {schema}; Owner: -
--

CREATE INDEX imsaccommodationboard_hotelid ON {schema}.imsaccommodationboard USING btree (hotel_id);


--
-- Name: imsaccommodationcnxpolicy_hotelid; Type: INDEX; Schema: {schema}; Owner: -
--

CREATE INDEX imsaccommodationcnxpolicy_hotelid ON {schema}.imsaccommodationcnxpolicy USING btree (hotel_id);


--
-- Name: imsaccommodationrate_hotelid; Type: INDEX; Schema: {schema}; Owner: -
--

CREATE INDEX imsaccommodationrate_hotelid ON {schema}.imsaccommodationrate USING btree (hotel_id);


--
-- Name: imsaccommodationrc_countrycode; Type: INDEX; Schema: {schema}; Owner: -
--

CREATE INDEX imsaccommodationrc_countrycode ON {schema}.imsaccommodationrc USING btree (country_code);


--
-- Name: imsaccommodationrc_latlong; Type: INDEX; Schema: {schema}; Owner: -
--

CREATE INDEX imsaccommodationrc_latlong ON {schema}.imsaccommodationrc USING btree (latitude, longitude);


--
-- Name: imsaccommodationrc_olerycompanycode; Type: INDEX; Schema: {schema}; Owner: -
--

CREATE INDEX imsaccommodationrc_olerycompanycode ON {schema}.imsaccommodationrc USING btree (olery_company_code);


--
-- Name: imsaccommodationroomtype_hotelid; Type: INDEX; Schema: {schema}; Owner: -
--

CREATE INDEX imsaccommodationroomtype_hotelid ON {schema}.imsaccommodationroomtype USING btree (hotel_id);


--
-- Name: imsaccommodationseason_hotelid; Type: INDEX; Schema: {schema}; Owner: -
--

CREATE INDEX imsaccommodationseason_hotelid ON {schema}.imsaccommodationseason USING btree (hotel_id);


--
-- Name: imsaccommodationspecial_hotelid; Type: INDEX; Schema: {schema}; Owner: -
--

CREATE INDEX imsaccommodationspecial_hotelid ON {schema}.imsaccommodationspecial USING btree (hotel_id);


--
-- Name: imsactivitysupplierageband fk2gk4py55dpcevc78kpo4cbq4b; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivitysupplierageband
    ADD CONSTRAINT fk2gk4py55dpcevc78kpo4cbq4b FOREIGN KEY (activity_supplier_id) REFERENCES {schema}.imsactivitysupplier(id);


--
-- Name: imseventmerchandiselink fk3l2m881us539omn3y76q3lq8g; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventmerchandiselink
    ADD CONSTRAINT fk3l2m881us539omn3y76q3lq8g FOREIGN KEY (eventseries_id) REFERENCES {schema}.imseventseries(id);


--
-- Name: imslocation fk4gohipn0dslv077dgq4h31gx0; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imslocation
    ADD CONSTRAINT fk4gohipn0dslv077dgq4h31gx0 FOREIGN KEY (country_id) REFERENCES {schema}.imscountry(countryid);


--
-- Name: imstransportationbasicsegment fk50klcrxp5bo1nqfoe29deqhkb; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imstransportationbasicsegment
    ADD CONSTRAINT fk50klcrxp5bo1nqfoe29deqhkb FOREIGN KEY (transport_id) REFERENCES {schema}.imstransportationbasic(id);


--
-- Name: imsactivitydeparturetime fk54u92wkdmbd3fkps7vjgm4qd8; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivitydeparturetime
    ADD CONSTRAINT fk54u92wkdmbd3fkps7vjgm4qd8 FOREIGN KEY (activity_id) REFERENCES {schema}.imsactivity(id);


--
-- Name: imsmerchandise fk7k2q4nec0epi6ycll8xk30a17; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsmerchandise
    ADD CONSTRAINT fk7k2q4nec0epi6ycll8xk30a17 FOREIGN KEY (merchandisecategory_id) REFERENCES {schema}.imsmerchandisecategory(id);


--
-- Name: imsevent fk814nq43earbddovbqyft8grt2; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsevent
    ADD CONSTRAINT fk814nq43earbddovbqyft8grt2 FOREIGN KEY (eventvenue_id) REFERENCES {schema}.imseventvenue(id);


--
-- Name: imsevent fk901o9j02iwh7ueo0kxo6tv858; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsevent
    ADD CONSTRAINT fk901o9j02iwh7ueo0kxo6tv858 FOREIGN KEY (eventsupplier_id) REFERENCES {schema}.imseventsupplier(id);


--
-- Name: imsmerchandisesale fk90aaop6peos2v9m1qbrb49e3s; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsmerchandisesale
    ADD CONSTRAINT fk90aaop6peos2v9m1qbrb49e3s FOREIGN KEY (merchandise_id) REFERENCES {schema}.imsmerchandise(id);


--
-- Name: imsactivity fkadd4njt914qjghy60eixphnrj; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivity
    ADD CONSTRAINT fkadd4njt914qjghy60eixphnrj FOREIGN KEY (activitysupplier_id) REFERENCES {schema}.imsactivitysupplier(id);


--
-- Name: imsactivitysale fkat7wi59sjp9ouffvx2cew3bfk; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivitysale
    ADD CONSTRAINT fkat7wi59sjp9ouffvx2cew3bfk FOREIGN KEY (activity_id) REFERENCES {schema}.imsactivity(id);


--
-- Name: imseventallotment fkca60tlcmmoxmdsxypwxpesrqd; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventallotment
    ADD CONSTRAINT fkca60tlcmmoxmdsxypwxpesrqd FOREIGN KEY (event_id) REFERENCES {schema}.imsevent(id);


--
-- Name: imstransportationbasicclass fkd5p44n6qi2ijg0a116hdar435; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imstransportationbasicclass
    ADD CONSTRAINT fkd5p44n6qi2ijg0a116hdar435 FOREIGN KEY (transport_id) REFERENCES {schema}.imstransportationbasic(id);


--
-- Name: imsactivityoption fkdv863t11swernlyvn25s2ytxo; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivityoption
    ADD CONSTRAINT fkdv863t11swernlyvn25s2ytxo FOREIGN KEY (activity_id) REFERENCES {schema}.imsactivity(id);


--
-- Name: imsevent fkdydqai9kxp4ecjtcidbymwhq4; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsevent
    ADD CONSTRAINT fkdydqai9kxp4ecjtcidbymwhq4 FOREIGN KEY (eventseries_id) REFERENCES {schema}.imseventseries(id);


--
-- Name: imseventclassification fkg7f340ksbyyfvp91pyc76hogc; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventclassification
    ADD CONSTRAINT fkg7f340ksbyyfvp91pyc76hogc FOREIGN KEY (event_id) REFERENCES {schema}.imsevent(id);


--
-- Name: imseventmerchandiselink fkgom2ynkow3cu9h805mgt88gb6; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventmerchandiselink
    ADD CONSTRAINT fkgom2ynkow3cu9h805mgt88gb6 FOREIGN KEY (merchandise_id) REFERENCES {schema}.imsmerchandise(id);


--
-- Name: imseventseries fklxci2l5vwh5uamsdkyit8mpy8; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventseries
    ADD CONSTRAINT fklxci2l5vwh5uamsdkyit8mpy8 FOREIGN KEY (eventtype_id) REFERENCES {schema}.imseventtype(id);


--
-- Name: imsactivityoptionblock fknhyid4yot5k0npuxf14ty7h1n; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsactivityoptionblock
    ADD CONSTRAINT fknhyid4yot5k0npuxf14ty7h1n FOREIGN KEY (activity_id) REFERENCES {schema}.imsactivityoptionblock(id);


--
-- Name: imsmerchandise fkpfow65838qds6mvrnjyix62hp; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsmerchandise
    ADD CONSTRAINT fkpfow65838qds6mvrnjyix62hp FOREIGN KEY (merchandisesupplier_id) REFERENCES {schema}.imsmerchandisesupplier(id);


--
-- Name: imsmerchandiseoption fkqmog4qwwwr2x380cflqutjro8; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imsmerchandiseoption
    ADD CONSTRAINT fkqmog4qwwwr2x380cflqutjro8 FOREIGN KEY (merchandise_id) REFERENCES {schema}.imsmerchandise(id);


--
-- Name: imseventsale fkqo84593suq8qrxx10x5jc18p7; Type: FK CONSTRAINT; Schema: {schema}; Owner: -
--

ALTER TABLE ONLY {schema}.imseventsale
    ADD CONSTRAINT fkqo84593suq8qrxx10x5jc18p7 FOREIGN KEY (event_id) REFERENCES {schema}.imsevent(id);



