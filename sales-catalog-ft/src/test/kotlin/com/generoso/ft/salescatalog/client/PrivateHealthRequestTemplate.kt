package com.generoso.ft.salescatalog.client

import com.generoso.ft.salescatalog.client.model.Endpoint
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
@Qualifier("private")
class PrivateHealthRequestTemplate @Autowired constructor(
    @Value("\${service.host}") host: String?,
    @Value("\${service.context-path:}") contextPath: String?
) : RequestTemplate(host!!, contextPath!!) {

    override val endpoint: Endpoint
        get() = Endpoint.PRIVATE_HEALTH_CHECK
}
