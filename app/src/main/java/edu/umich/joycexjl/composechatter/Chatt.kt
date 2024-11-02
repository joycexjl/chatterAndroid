package edu.umich.joycexjl.composechatter

import java.util.UUID

class Chatt(var username: String? = null,
            var message: String? = null,
            var id: UUID? = null,
            var timestamp: String? = null,
            var altRow: Boolean = true)