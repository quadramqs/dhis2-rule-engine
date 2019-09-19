package org.hisp.dhis.rules

import org.hisp.dhis.rules.models.*
import java.util.*
import java.util.concurrent.Callable

// ToDo: logging
actual class RuleEngine actual constructor(
        val executionContext: RuleEngineContext,
        val events: List<RuleEvent>,
        val ruleEnrollment: RuleEnrollment?,
        val triggerEnvironment: TriggerEnvironment?) {

    actual fun evaluate(ruleEvent: RuleEvent?): Callable<List<RuleEffect>> {
        ruleEvent?.let {
            events.forEach {
                check(it.event != ruleEvent.event) { "Event '${it.event}' is already set as a part of execution context." }
            }
            return internalEvaluate(ruleEvent, executionContext.rules)
        } ?: throw IllegalArgumentException("ruleEvent == null")
    }

    actual fun evaluate(ruleEvent: RuleEvent?, rulesToEvaluate: List<Rule>): Callable<List<RuleEffect>> {
        ruleEvent?.let {
            events.forEach {
                check(it.event != ruleEvent.event) { "Event '${it.event}' is already set as a part of execution context." }
            }
            return internalEvaluate(ruleEvent, rulesToEvaluate)
        } ?: throw IllegalArgumentException("ruleEvent == null")

    }

    actual fun evaluate(ruleEnrollment: RuleEnrollment?, rulesToEvaluate: List<Rule>): Callable<List<RuleEffect>> {
        return when {
            ruleEnrollment == null -> throw IllegalArgumentException("ruleEnrollment == null")
            this.ruleEnrollment != null ->
                throw IllegalStateException("Enrollment '${this.ruleEnrollment.enrollment}' is already set " +
                        "as a part of execution context.")
            else -> internalEvaluate(ruleEnrollment, rulesToEvaluate)
        }
    }

    actual fun evaluate(ruleEnrollment: RuleEnrollment?): Callable<List<RuleEffect>> {
        return when {
            ruleEnrollment == null -> throw IllegalArgumentException("ruleEnrollment == null")
            this.ruleEnrollment != null ->
                throw IllegalStateException("Enrollment '${this.ruleEnrollment.enrollment}' is already set as a " +
                        "part of execution context.")
            else -> internalEvaluate(ruleEnrollment, executionContext.rules)
        }
    }

    private fun internalEvaluate(ruleEvent: RuleEvent, rulesToEvaluate: List<Rule>): RuleEngineExecution {

        val valueMap = RuleVariableValueMapBuilder.target(ruleEvent)
                .ruleVariables(executionContext.ruleVariables)
                .ruleEnrollment(ruleEnrollment)
                .triggerEnvironment(triggerEnvironment)
                .ruleEvents(events)
                .calculatedValueMap(executionContext.calculatedValueMap)
                .constantValueMap(executionContext.constantsValues)
                .build()

        return RuleEngineExecution(executionContext.expressionEvaluator,
                rulesToEvaluate, valueMap, executionContext.supplementaryData)
    }

    private fun internalEvaluate(ruleEnrollment: RuleEnrollment, rulesToEvaluate: List<Rule>): RuleEngineExecution {

        val valueMap = RuleVariableValueMapBuilder.target(ruleEnrollment)
                .ruleVariables(executionContext.ruleVariables)
                .triggerEnvironment(triggerEnvironment)
                .ruleEvents(events)
                .constantValueMap(executionContext.constantsValues)
                .build()

        return RuleEngineExecution(executionContext.expressionEvaluator,
                rulesToEvaluate, valueMap, executionContext.supplementaryData)
    }

    actual class Builder actual constructor(private val executionContext: RuleEngineContext) {

        private var ruleEvents: List<RuleEvent>? = null

        private var ruleEnrollment: RuleEnrollment? = null

        private var triggerEnvironment: TriggerEnvironment? = null

        actual fun events(ruleEvents: List<RuleEvent>?) =
                apply {
                    ruleEvents?.let { this.ruleEvents = Collections.unmodifiableList(ruleEvents) } ?:
                    throw IllegalArgumentException("ruleEvents == null")
                }


        actual fun enrollment(ruleEnrollment: RuleEnrollment?) =
                apply {
                    ruleEnrollment?.let { this.ruleEnrollment = it } ?:
                    throw IllegalArgumentException("ruleEnrollment == null")
                }

        actual fun triggerEnvironment(triggerEnvironment: TriggerEnvironment?) =
                apply {
                    triggerEnvironment?.let { this.triggerEnvironment = triggerEnvironment } ?:
                    throw IllegalArgumentException("triggerEnvironment == null")
                }

        actual fun build() =
                RuleEngine(executionContext,
                        ruleEvents ?: listOf(),
                        ruleEnrollment,
                        triggerEnvironment)
    }
}
