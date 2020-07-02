package me.reasonless.codeutilities.objects;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.reasonless.codeutilities.CodeUtilities;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.io.FileUtils;

public class AutoCompleteArgType implements ArgumentType<String> {

  public static AutoCompleteArgType arg() {
    return new AutoCompleteArgType();
  }

  @Override
  public String parse(StringReader reader) throws CommandSyntaxException {
    String text = reader.getRemaining();
    reader.setCursor(reader.getTotalLength());
    return text;
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
      SuggestionsBuilder builder) {
    String input = context.getInput().replaceFirst("/var ", "");
    CodeUtilities.infoMsgYellow("Input: " + input);
    try {
      File f = new File("CodeUtilities/AutoCompletion/" + CodeUtilities.plotPos.getX() + " "
          + CodeUtilities.plotPos.getZ() + ".ac");
      if (!f.exists()) {
        f.createNewFile();
      }// 0 Width space: "​"
      List<String> suggestions = Files.readAllLines(Paths.get(f.toURI()));

      System.out.println(suggestions);
      for (String sug : sortByOccurrences(suggestions)) {
        if (sug.startsWith(input) || input.length() == 0) {
          CodeUtilities.infoMsgYellow("Match: " + sug);
          builder.suggest(sug);
        } else
          CodeUtilities.infoMsgYellow("NoMatch: " + sug);
      }
    } catch (Exception e) {
      CodeUtilities.errorMsg("§cAn Error occurred while trying to load the AutoCompletion data!");
      e.printStackTrace();
    }
    CompletableFuture<Suggestions> cf = new CompletableFuture<>();
    cf.complete(builder.build());
    return cf;
  }

  //Credit: https://stackoverflow.com/questions/11504902/simple-way-to-count-occurrences-of-string-and-sort-by-them
  public List<String> sortByOccurrences(List<String> list) {
    Map<String, Integer> map = new HashMap<String, Integer>();
    for (String s : list) {
      if (map.containsKey(s)) {
        map.put(s, map.get(s) + 1);
      } else {
        map.put(s, 1);
      }
    }
    ValueComparator<String, Integer> comparator = new ValueComparator<String, Integer>(map);
    Map<String, Integer> sortedMap = new TreeMap<String, Integer>(comparator);
    sortedMap.putAll(map);
    return new ArrayList<String>(sortedMap.keySet());
  }


}
