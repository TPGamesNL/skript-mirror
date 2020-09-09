package com.btk5h.skriptmirror.skript.custom.expression;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.util.StringUtils;
import com.btk5h.skriptmirror.skript.custom.CustomSyntaxSection;
import com.btk5h.skriptmirror.util.SkriptReflection;
import com.btk5h.skriptmirror.util.SkriptUtil;

import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;

@Name("Define Constant")
@Description({"For more information on skript-reflect, you can visit the wiki here: https://tpgamesnl.gitbook.io/skript-reflect/"})
public class CustomConstantSection extends CustomSyntaxSection<ConstantSyntaxInfo> {
  static {
    CustomSyntaxSection.register("Define Constant", CustomConstantSection.class,
        "option <.+>");
    // TODO add support for custom constant expressions
  }

  private static final DataTracker<ConstantSyntaxInfo> dataTracker = new DataTracker<>();

  static {
    dataTracker.setSyntaxType("constant");

    // noinspection unchecked
    Skript.registerExpression(CustomExpression.class, Object.class, ExpressionType.SIMPLE);
    Optional<ExpressionInfo<?, ?>> info = StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(Skript.getExpressions(), Spliterator.ORDERED), false)
        .filter(i -> i.c == CustomExpression.class)
        .findFirst();
    info.ifPresent(dataTracker::setInfo);
  }

  @Override
  protected DataTracker<ConstantSyntaxInfo> getDataTracker() {
    return dataTracker;
  }

  @SuppressWarnings({"unchecked", "SwitchStatementWithTooFewBranches"})
  @Override
  protected boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult, SectionNode node) {
    String what;

    switch (matchedPattern) {
      case 0:
        what = parseResult.regexes.get(0).group();

        AtomicBoolean hasGetSection = new AtomicBoolean();
        boolean nodesOkay = handleEntriesAndSections(node,
          entryNode -> false,
          sectionNode -> {
            String key = sectionNode.getKey();
            assert key != null;

            if (key.equalsIgnoreCase("get")) {
              ScriptLoader.setCurrentEvent("custom constant getter", ConstantGetEvent.class);
              List<TriggerItem> items = SkriptUtil.getItemsFromNode(sectionNode);
              Trigger getter =
                  new Trigger(SkriptUtil.getCurrentScript(), "get {@" + what + "}", this, items);

              computeOption(what, getter);

              hasGetSection.set(true);
              return true;
            }

            return false;
          });

        if (!hasGetSection.get())
          Skript.warning("Computed options don't work without a get section");

        return nodesOkay;
    }

    return false;
  }

  private static void computeOption(String option, Trigger getter) {
    ConstantGetEvent constantEvent = new ConstantGetEvent(0, null);
    getter.execute(constantEvent);
    SkriptReflection.getCurrentOptions().put(option, StringUtils.join(constantEvent.getOutput()));
  }
}

