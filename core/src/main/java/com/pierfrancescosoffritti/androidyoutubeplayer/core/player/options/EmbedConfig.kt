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
//            private const val EMBED_CONFIG = "embedConfig"
//            private const val ADS_CONFIG = "adsConfig"
//            private const val AD_TAG_PARAMETERS = "adTagParameters"
//            private const val NON_PERSONALIZED_AD = "nonPersonalizedAd"
            private const val IU = "iu"
            private const val WIDTH = "width"
            private const val HEIGTH = "height"
            private const val CUST_PARAMS = "cust_params"
            private const val ENABLE_IMA = "enable_ima"

        }

        private val embedConfig = JSONObject()
//        private val adsConfig = JSONObject()
//        private val adTagParameters = JSONObject()

        init {
//            adsConfig.put(NON_PERSONALIZED_AD, true)
        }

        fun build(): EmbedConfig {
//            adsConfig.put(AD_TAG_PARAMETERS, adTagParameters)
//            embedConfig.put(ADS_CONFIG, adsConfig)
            return EmbedConfig(embedConfig)
        }

        fun iu(values: String): Builder {
//            adTagParameters.put(IU, values)
            embedConfig.put(IU, values)
            return this
        }

        fun custParams(custParams: String): Builder {
            embedConfig.put(CUST_PARAMS, custParams)
            return this
        }

        fun enableIMA(enableIMA: Boolean): Builder {
            embedConfig.put(ENABLE_IMA, enableIMA)
            return this
        }

        /**
         * @param width player width in px, -1 mean 100%
         * @param height player height in px, -1 mean 100%
         */
        fun size(width: Int, height: Int): Builder {
//            adTagParameters.put(IU, values)

            fun convertToString(size: Int) = when {
                width >= 0 -> "${size}px"
                else -> "100%"
            }

            embedConfig.put(WIDTH, convertToString(width))
            embedConfig.put(HEIGTH, convertToString(height))
            return this
        }
    }
}
