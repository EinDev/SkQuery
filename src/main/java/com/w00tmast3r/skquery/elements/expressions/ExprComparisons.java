package com.w00tmast3r.skquery.elements.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.SimpleExpression;
import org.skriptlang.skript.lang.comparator.Comparator;
import ch.njol.util.Kleenean;

import com.w00tmast3r.skquery.annotations.Patterns;
import com.w00tmast3r.skquery.util.Collect;

import org.bukkit.event.Event;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

@Patterns({"%object%[ ]===[ ]%object%",
        "%object%[ ]==[ ]%object%",
        "%object%[ ]\\>[ ]%object%",
        "%object%[ ]\\<[ ]%object%",
        "%object%[ ]\\>=[ ]%object%",
        "%object%[ ]\\<=[ ]%object%"})
public class ExprComparisons extends SimpleExpression<Boolean> {

    private Expression<?> first, second;
    int match;

    @Override
    protected Boolean[] get(Event e) {
        Relation r = Comparators.compare(first.getSingle(e), second.getSingle(e));
        switch (match) {
            case 0:
                return Collect.asArray(Relation.EQUAL.isImpliedBy(r));
            case 1:
                return Collect.asArray((first.getSingle(e) + "").equals(second.getSingle(e) + ""));
            case 2:
                return Collect.asArray(Relation.GREATER.isImpliedBy(r));
            case 3:
                return Collect.asArray(Relation.SMALLER.isImpliedBy(r));
            case 4:
                return Collect.asArray(Relation.GREATER_OR_EQUAL.isImpliedBy(r));
            case 5:
                return Collect.asArray(Relation.SMALLER_OR_EQUAL.isImpliedBy(r));
        }
        return null;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "compare";
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        first = exprs[0];
        second = exprs[1];
        match = matchedPattern;
        if (first instanceof Variable && second instanceof Variable) {
            first = first.getConvertedExpression(Object.class);
            second = second.getConvertedExpression(Object.class);
        } else if (first instanceof Literal<?> && second instanceof Literal<?>) {
            first = first.getConvertedExpression(Object.class);
            second = second.getConvertedExpression(Object.class);
            if (first == null || second == null) return false;
        } else {
            if (first instanceof Literal<?>) {
                first = first.getConvertedExpression(second.getReturnType());
                if (first == null) return false;
            } else if (second instanceof Literal<?>) {
                second = second.getConvertedExpression(first.getReturnType());
                if (second == null) return false;
            }
            if (first instanceof Variable) {
                first = first.getConvertedExpression(second.getReturnType());
            } else if (second instanceof Variable) {
                second = second.getConvertedExpression(first.getReturnType());
            }
            assert first != null && second != null;
        }
        return true;
    }
}
