package org.hisp.dhis.rules.functions

/*
 * Copyright (c) 2004-2018, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hisp.dhis.rules.RuleVariableValue

/**
 * return minimum value of provided data element present in entire enrollment
 */
class RuleFunctionMinValue : RuleFunction() {

    override fun evaluate(arguments: List<String?>, valueMap: Map<String, RuleVariableValue>?,
                          supplementaryData: Map<String, List<String>>?): String {
        return when {
            valueMap == null -> throw IllegalArgumentException("valueMap is expected")
            arguments.size != 1 -> throw IllegalArgumentException("One argument was expected, ${arguments.size} were supplied")
            else -> getMinValue(arguments, valueMap)
        }
    }

    private fun getMinValue(arguments: List<String?>, valueMap: Map<String, RuleVariableValue>): String {
        val dataElement = arguments[0]

        return when {
            valueMap.containsKey(dataElement) -> {
                val ruleVariableValue = valueMap[dataElement]

                val values = ruleVariableValue?.candidates

                val doubles = values?.asSequence()?.map { it.toDouble() }

                doubles?.min().toString()
            }
            else -> ""
        }

    }

    companion object {
        const val D2_MIN_VALUE = "d2:minValue"

        @JvmStatic
        fun create() = RuleFunctionMinValue()
    }
}
