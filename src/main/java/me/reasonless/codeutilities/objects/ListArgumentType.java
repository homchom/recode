package me.reasonless.codeutilities.objects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ListArgumentType implements ArgumentType<String> {

  private Collection<String> list = new ArrayList<>();

  public ListArgumentType(Set<String> list) {
    this.list.addAll(list);
  }

  public ListArgumentType(List<String> list) {
    this.list.addAll(list);
  }

  public static ListArgumentType list(List<String> list) {
    return new ListArgumentType(list);
  }

  public static ListArgumentType list(Set<String> list) {
    return new ListArgumentType(list);
  }

  @Override
  public String parse(final StringReader reader) throws CommandSyntaxException {
    String str = reader.readUnquotedString();
    if (list.contains(str)) {
      return str;
    }
    throw new SimpleCommandExceptionType(() -> "Unknown Setting!").create();
  }

  @Override
  public Collection<String> getExamples() {
    return list;
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
      SuggestionsBuilder builder) {

    for (String str : list) {
      builder.suggest(str);
    }

    CompletableFuture<Suggestions> cf = new CompletableFuture<>();
    cf.complete(builder.build());
    return cf;
  }
}