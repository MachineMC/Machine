package org.machinemc

import java.net.URI

/**
 * Utility class for providing checkstyle configuration across modules.
 */
object CheckStyleProvider {

    /**
     * @return URI for the checkstyle configuration
     */
    fun get(): URI {
        return CheckStyleProvider::class.java.getResource("/machine_checks.xml")!!.toURI()
    }

}