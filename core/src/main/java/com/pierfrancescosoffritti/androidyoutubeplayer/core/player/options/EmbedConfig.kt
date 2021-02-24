package com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options

import org.json.JSONObject

class EmbedConfig private constructor(private val embedConfig: JSONObject) {

    companion object {
        val default = Builder().build()
    }

    override fun toString(): String {
        return embedConfig.toString()
    }

    class Builder {
        companion object {
            private const val EMBED_CONFIG = "embedConfig"
            private const val ADS_CONFIG = "adsConfig"
            private const val AD_TAG_PARAMETERS = "adTagParameters"
            private const val NON_PERSONALIZED_AD = "nonPersonalizedAd"
            private const val IU = "iu"
        }

        private val embedConfig = JSONObject()
        private val adsConfig = JSONObject()
        private val adTagParameters = JSONObject()

        init {
            adsConfig.put(NON_PERSONALIZED_AD, true)
        }

        fun build(): EmbedConfig {
            adsConfig.put(AD_TAG_PARAMETERS, adTagParameters)
            embedConfig.put(ADS_CONFIG, adsConfig)
            return EmbedConfig(embedConfig)
        }

        fun iu(values: String): Builder {
            adTagParameters.put(IU, values)
            return this
        }
    }
}
