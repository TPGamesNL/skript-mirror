package com.btk5h.skriptmirror.skript.custom.condition;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

@Name("Negate Condition")
@Description({"You can find more information about this here: https://tpgamesnl.gitbook.io/skript-reflect/advanced/custom-syntax/conditions"})
public class EffNegateCondition extends Effect {
  static {
    Skript.registerEffect(EffNegateCondition.class, "negate [the] [current] condition");
  }

  @Override
  protected void execute(Event e) {
    ((ConditionCheckEvent) e).markNegated();
  }

  @Override
  public String toString(Event e, boolean debug) {
    return "negate condition";
  }

  @Override
  public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed,
                      SkriptParser.ParseResult parseResult) {
    if (!ScriptLoader.isCurrentEvent(ConditionCheckEvent.class)) {
      Skript.error("The effect 'negate condition' may only be used in a custom condition.",
          ErrorQuality.SEMANTIC_ERROR);
      return false;
    }
    return true;
  }
}
