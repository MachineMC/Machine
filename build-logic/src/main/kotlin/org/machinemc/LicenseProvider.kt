package org.machinemc

import java.net.URI

/**
 * Utility class for providing checkstyle configuration across modules.
 */
object LicenseProvider {

    /**
     * @return URI for the checkstyle configuration
     */
    fun get(): URI {
        return LicenseProvider::class.java.getResource("/HEADER.txt")!!.toURI()
    }

}