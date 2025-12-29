package wallapp.error

enum class ErrorStatusCode(val code: Int) {
  // app specific codes
  Unknown(0),
  ImageLoadError(1),
  NoConnection(2),

  // HTTP error status codes
  BadRequest(400),
  Unauthorized(401),
  PaymentRequired(402),
  Forbidden(403),
  NotFound(404),
  MethodNotAllowed(405),
  NotAcceptable(406),
  ProxyAuthenticationRequired(407),
  RequestTimeout(408),
  Conflict(409),
  Gone(410),
  LengthRequired(411),
  PreconditionFailed(412),
  PayloadTooLarge(413),
  URITooLong(414),
  UnsupportedMediaType(415),
  RangeNotSatisfiable(416),
  ExpectationFailed(417),
  IAmATeapot(418),
  MisdirectedRequest(421),
  UnprocessableEntity(422),
  Locked(423),
  FailedDependency(424),
  UpgradeRequired(426),
  PreconditionRequired(428),
  TooManyRequests(429),
  RequestHeaderFieldsTooLarge(431),
  UnavailableForLegalReasons(451),

  InternalServerError(500),
  NotImplemented(501),
  BadGateway(502),
  ServiceUnavailable(503),
  GatewayTimeout(504),
  HTTPVersionNotSupported(505),
  VariantAlsoNegotiates(506),
  InsufficientStorage(507),
  LoopDetected(508),
  NotExtended(510),
  NetworkAuthenticationRequired(511);

  companion object {
    private val map = values().associateBy(ErrorStatusCode::code)
    fun fromCode(code: Int) = map[code] ?: Unknown
  }
}