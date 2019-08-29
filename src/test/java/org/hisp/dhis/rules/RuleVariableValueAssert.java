package org.hisp.dhis.rules;

import org.hisp.dhis.rules.models.RuleValueType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.assertj.core.api.Java6Assertions.assertThat;

final class RuleVariableValueAssert
{
        private final RuleVariableValue variableValue;

        private RuleVariableValueAssert( RuleVariableValue variableValue )
        {
                this.variableValue = variableValue;
        }

        @Nonnull
        static RuleVariableValueAssert assertThatVariable( @Nonnull RuleVariableValue variableValue )
        {
                return new RuleVariableValueAssert( variableValue );
        }

        @Nonnull
        RuleVariableValueAssert hasValue( @Nullable String value )
        {
                assertThat( variableValue.getValue() ).isEqualTo( value );
                return this;
        }

        @Nonnull
        RuleVariableValueAssert hasCandidates( @Nonnull String... candidates )
        {
                assertThat( variableValue.getCandidates().size() ).isEqualTo( candidates.length );
                for ( int index = 0; index < candidates.length; index++ )
                {
                        assertThat( variableValue.getCandidates().get( index ) ).isEqualTo( candidates[index] );
                }
                return this;
        }

        @Nonnull
        RuleVariableValueAssert isTypeOf( @Nonnull RuleValueType valueType )
        {
                assertThat( variableValue.getType() ).isEqualTo( valueType );
                return this;
        }
}
