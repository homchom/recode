package io.github.codeutilities.commands.schem;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.schem.Litematica;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.ItemUtil;
import io.github.codeutilities.util.TemplateUtils;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

import java.io.File;

public class SchemCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(subcommand(mc, ArgBuilder.literal("schem")));
        cd.register(subcommand(mc, ArgBuilder.literal("litematic")));
        cd.register(subcommand(mc, ArgBuilder.literal("schematic")));
    }

    public LiteralArgumentBuilder<CottonClientCommandSource> subcommand(MinecraftClient mc, LiteralArgumentBuilder<CottonClientCommandSource> literal){
           literal.then(ArgumentBuilders.argument("filepath", StringArgumentType.greedyString())
                .executes(ctx -> {
                    if (mc.player.isCreative()){
                        String arg = StringArgumentType.getString(ctx, "filepath");
                    File target = new File("schematics/" + arg);
                    String format = "";

                    if (!target.getName().endsWith(".litematic") && !target.getName().endsWith(".schematic") && !target.getName().endsWith(".schem")) {
                        if (new File("schematics/" + arg + ".litematic").exists()) {
                            target = new File("schematics/" + arg + ".litematic");
                            format = "litematic";
                        }
                        if (new File("schematics/" + arg + ".schematic").exists()) {
                            target = new File("schematics/" + arg + ".schematic");
                            format = "schematic";
                        }
                        if (new File("schematics/" + arg + ".schem").exists()) {
                            target = new File("schematics/" + arg + ".schem");
                            format = "schem";
                        }
                    } else {
                        if (target.getName().endsWith(".litematic")) {
                            format = "litematic";
                        }
                        if (target.getName().endsWith(".schematic")) {
                            format = "schematic";
                        }
                        if (target.getName().endsWith(".schem")) {
                            format = "schem";
                        }
                    }

                    if (target.exists()) {
                        System.out.println(target.exists());
                        if (format == "litematic") {
                            System.out.println("its a litematic");
                            File finalTarget = target;
                            new Thread(() -> {
                                try {
                                    String template = Litematica.parse(finalTarget);
                                    System.out.println(template);
                                } catch (Exception e) {
                                    ChatUtil.sendMessage("An error occurred while executing this command.", ChatType.FAIL);
                                    e.printStackTrace();
                                }
                            }).start();

                        } else if (format == "schematic") {
                            System.out.println("its a schematic");
                            File finalTarget = target;
                            new Thread(() -> {
                                try {
                                    //String template = Schematica.parse(finalTarget);
                                    //System.out.println(template);
                                } catch (Exception e) {
                                    ChatUtil.sendMessage("An error occurred while executing this command.", ChatType.FAIL);
                                    e.printStackTrace();
                                }
                            }).start();
                        } else {
                            ChatUtil.sendMessage("The file has to be a litematic, schematic, schem or vanilla nbt format.", ChatType.FAIL);
                        }
                    } else {
                        ChatUtil.sendMessage("The file §6" + arg + "§c was not found.", ChatType.FAIL);
                    }
                }else{
                    ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
                }
                    return 1;
                }))
                .then(ArgBuilder.literal("loader")
                        .executes(ctx -> {
                            if (mc.player.isCreative()) {
                                ItemStack stack = new ItemStack(Items.STICKY_PISTON);
                                String templateData = "H4sIAAAAAAAAAO18ybLkNpblrzyLTS0Ymc55iO5qM86Tk3QOzimVJuPkJJ3zPMi0q9/odX5E7/JT+kvan6QspbIUmS0rVZW6LXzxnAABXNwD4J57Afj75kNcd0k1ffj0h28+lOmHT9+nP3z84fvTh8fSJq9kNOavQq8yc9b8UPr19F3Oe63vEh8/pNEc/aXUK/cbTvhaZ5xPMEaiH8v001cfmrLNkjF6zJ+aKM/aOfp6mqNXXvp1XkfT9HUftdlXHz7OUf7pm7Sc+jo6Pn2jR0326Z+++epDts9j9NW79K8+xF2dvh4fUT1lH796SYzqMvmrjKVNs7F+b/qvMqd5LKtsLsZuyYu/yu/ixzIl0fyTwvNL3Cv51YevPnz7x79J/9O3335ku6WdP0Hxtx9eiQ9T3c0fPoHffvyCzM8iA31B5jPIwF+Q+QwyyBdkPoMM+gWZzyCDfUHmM8jgX5D5DDLEF2Q+gwz5BZnPIEP9CsjMWznNZZt/vb6ypl+Exjwu/0FgJF3djd9pHw3LS+pf42EnRdbAnPBzOH28dmP26Q/v/f1p/n/+eP6owpHVdbf9VAk2quu36O3Vt4/f/Iz4Hyvn33X0J1W7NLvPZV3O5ft4/aMGfk76716LIRvfO/z2HlrNZde+Pcpxmn//s6D+1sALuuUtidq3KBmWcszeujZ7W6bXHP4lvf/PmLw/1/nL9D5/3/77o6yz9rW43sr27ff/uhy/fxvNZTL9j89q89sZnXyMjr9ZnUvfd+M8vf3+O00+/vD9rtFb1KZvv6/fbdJ78ufU++O3H6UyzYQ6yqdP0Ee+TYqonZuX6Z5euv2N5aqX8d1612v9CZq+/eNnQr4v0fBno+FfIxzO3rv+9WuIp/n/Ae4Q/mLtfrCb/2hgfmwrjcbq63/b4J//1/+FDf8Mh70vgmsXpT87ZB9vS/xqj1mqqpzdqF6y6dNLSnH02ZgscfYpedHAa3heSL/Uf43Xq+o71NEyFz+ItY6ovb7W3Hei303N9z3+U/znP9X/CsSf/4S8dHhl/k2XXlXWbJxeRV61oO/USb9vQEInmf7Lx4BC33/EOvMEVGwoZtlLRoOIxv550xuo72zoeTqBKD0bdXTKcmru1eKtSjIlwg3lOOGRUSiTbmmfPab6wqpHqhNO/nj2N3pfcsy2byOKICA+i08Xp2Krh3YM2iJoTeacGETIBuM+xS4HXPC5qQtXbu8YEbyT/jKj7oO0bpnKHj63m+NjltV2jGEOXuoFcscHoqPdPc0OLiTFnRHRzj56y7A7W2GiWKtgO+8Jd1nMZbHp4E5F4dTUWuOgRzOkQU8Fx4HEspKqgbDEMwri4UGQhSjIhxldG4GXjhzRegk1GRu1OO3AyiDNawV1IMskyX5QMOLMStXm3GjYt73y79dAt0a4GTrPYpmK6a5RQfNFkgABWAakmwYoVozEHJK1qrKA/xyr/IXDc9OBwCsL3AHYUTf6oQKHSoBCSyppEWR91tYJjXkR0ZUG0jObIA4lect4woOEC7QA9LrpsITJ3RqSu1J07hK9IpVPdVAV0bHIgjUl384ZV+SaYHMyUk+UXr2uROf6GNUvTJm4xPVcbuxVWTmQo8b6EnA9WnFYqNN2QBMdt2kWnYlB8dC05h43dYoK7PM6XjM9HefK1iI3iEBGSGXbhRPorpb8cRPMbGcG7mlVg865dctXfFAG9qbOTYWvpT+5gULiOdHjEUxR9nHrnyHsCbYhatURK64IscfWHIiBgCa05fwVjLhCvFpghpeqVqZ1wCFF/mR98e6m+w7fOpGnnAxkUObWRRluMzL+Eoe1++XWGYBnWRN2bXX9KIJhUIb74kpmkV/WWydluNTNV96cUCB31nW9riCSp8jtQoATSSFpORPAlj4kBqOA1N+PrPU5AsMyIaSo21OZQeAywVCSQqdNKiO4I7eGOU/Svueu7xwvESNmJR2zeFsjhhAMu5Us14vwjLsN2ppS8cxh3Tyr3aGuDBTUjwg+Q+jbUHXxQGkEWrYk4oDYki14xDRj6FtodGsAxb0BR0Ru9T0fiDWpsY6+3uWsWlv74ar1rVjzszuv3FJCnWuV3rFkV0/x9Rnp3VRkNEtF/BC4RtzNFTFkIBIAfgI+DBvIhU1hDemmQwjoJ/QkhGOxdn/FjueaVQJo4k0Inq03sDulFBPuYpmNIoaerCYinO6181G9Ns523mhx8+g4C4T0ST0BiLCpOmDgy+Ok7i3+GBe6MS+n04sksNzX5GZ52OGSPFTK9pqgk2/t7CMe7YUi7ag25OxahzUqgqcoq85YXBvEVK5qtg+RKdji0IRLL3o2kVhRwcKNqUJTH/CeHC3hi//jQAxsjhX0HcehLFdy1g0cJsx5tQHvaGwEDRme9UVGLorsEk2+puo9OXYaty6xnjI40WleYJl2ymcSiyrJBdN2FVVASyUU0bIQk7X7WO9wnlQEgkcndmBhW7rbF1OAlpmGTZGntd0ieuSy48hGlVxGm5hRmJbCOumipvRDKouH8xobrgAbOgv4yBe50HKRVDBGdJAV7jQASQcdXsBRT/Yyjh+rUYpubRR5fqZx2aW8jokhpDmmXESj72QUXsituOQAEkMFsm1YlN+Tl3Fg4MysRaYPx93VMsjuneJE0q3kB6CwTEFjSEZL6i3dn2uoI6LAuBiP3T3E1rjmRqEXYI+mYzcc5zWxVaOUoQEDTcNAhodje4BJPBHPim7LeFM4FJetagSC42UrtYeULeT4GDgxWvRM4EF1WLC1LA4JQPQ8nZwmFM4aSxt48c52OE4AFkntcrm2CrrPGMbxvpJJ+Vx3Xv2cO/1I53xk/Op4mT6BvTjxfB8R9+5esOL+wovWQykWu6O1Ccx4yDAXPwgwuDcslZSjywE+EvtrXXjw2pfW6HV9c9/KZ733d1qSpDIwc/OhHlTI0WNyL0gbboc6KadgoJY6R3YgvPdEXRFmhp7Vk320Z2uui98F9MjHtlXYtMZzXBgjfEEsGyHAZb44NLA6KBJeRgeg+EdiSLwsXclbidqrd2qCdjOkm28ol+UCZCPiQ1ILP5HDPIj7JDuUvTzwdWzgOTcv7jaZg8sCjwfiSgTAxOkuFPaYcICwOACRoExjyzbVthQUW25ZGIbYIoixctOeN3NCXuCMKxqPa0f/wjxRGHc2Uslp+u95nL/GMcPPeJz/pb4S/AucpSD2Lo7GcC9nKRZmJmC9fInvqkXlV4npOb5yF5pr7NFfnNiTrsvRxuKAt5e2VaEUXq06chOQV8jIB86zAhIMwUCiZ+AeCL0aTchCcHg9JhNwuU165KSOJD80SqLtsxVNZELEnjpLF/C4XIu16cFPM41e2qtzQ4gHi61ErKOivKFlY+Y8Mhoryz11MZfrDhXyVdJilirvcaFpZ2xz5OIs2iBrsQAJiaBLVrzQc8TREznO0qzq9VOmUqUEeT4j5N6gBRMPS41REZXEj7PLx0RuLc8me1jy2gZqDUR/rfThDhsBzdjqy5amJjuIpBeU6p3GjaCWdR9jjmWzWIMGZDsIqvZ8TUW/lMgD5prkwKQOYOSQHIRngstox0SDBeoUGDPcVUvhe8Sy8zmsa6bGUNfQhEPMB0/Nme2Uom765Qzoh5apSqDehrW5+LGeU1IdGUfkrzR/XwL+RmlTPMvJMR7mZHARRB7Tsxtj27vlJe2bB6M3V6WbIQS8WEOqXQc3H4tp7bNuJlOtA/wXrq9hPfjQ2DubKgn0PnbeEAeTwsTkQMa0+oC16AIIJULH+KZvIt3gC4J3m6qtyKbctrOn3KSMJhlMlPsJb7ut790E7ijYhCWlFioLYyGlbc+X1yTvvGiwGKPeWiVBb6RHBotASbfZV8SD8snh2GaNyJB4RM3bLN4ib1AELUcU97Bai5wk8lL62q6vDbensHzELNnJ8nqcyzJjFphugMETWagoaAc4hpcO5YNKTT8knc08/brh2NygNmkTYi5PqoIx0uuRuItJZVjarqd6YTKTgBI6GfbAApXiAqFNY0uSjoActMjJskd8E+TTWE+BHV0JZTP6YEabArfYsZaiXp9IbXgEqH1ScNlO1w33zSW1PSTvqA4SvLym3KHE8DjGzuMJddol9fClgJmdmsNwI3opMrMEOqLztOMhe17UdsW7npLz+Amxbap3iIW5xPhyj9lxvyKpddSBIj68tvZqVbrSiRuQlBHVUDFxL9cvDVoqo9nG4BXAu5Y3eWufEslTGKJwPEFR/FAH0lgLqzvcGpFq1gsEL097DiJnqvZTA0CDGnclDtVGXD3/mSUScJBN0YTU4lvnbF48grne9Fig3GW7PUL5uVhEpWHPjPBcoqv9em22JoSJQIRf3hOgRaSZLfQ2XXG+Y90afLmDzIUjxVO3L5MQkWz0cGPzOuriiBwrnkp2aZybr2FADt2Huosn5v4yXv/8z9/HpV+i+18W3cO/ZEfm1zhs/rIj8+8eM+Q3uCWD/AI3w4vvl/h7NyOC2DAvd22m74cJELLbV3v+6PTJv9eekcDwbLRhZVDscLSXh2FZDds7IJMEQYvJBILBjwV4GFRfNvfVYPqHFDbyoO19cjzt+NFI4XV3harvUjEytLyFnk8fR3Z/vJtgsL08cU+AV9EfMeTp4BAaAhcOAIsquSvny8+Nodbdj26zAkaOjDS67lizFZ79zKSj4hKrjV70YhQlXqiKYw4LdIizGbjPMgWpbmg8cYXA5no3FWIqK1QWS5epgjJdBHcOQs3kfJ0YJ8o0KGdcaiw5U80Io6jWOFEW70CuFszAAN5Uyi//Qgtq1dBfNriVtUKi/RKYZNs/83bWex4woE7tIdi9e480qpNVgTyBdofeD0BkGMohGZqp0wS2GUZ2J10XGhpGdwgoLIAZsOODU8y2nIApKcRy8ViHuGEhsaSXqE/9wGt6YrsZvsFKcOMRqRwdNTjL8ZAmcJDFXX/tjjVhadc8HgsEtUqaMQKnPZuTFokurtKQgw4Y34+lOgJJkZlF5B/sw2SKXNUgaCCXqlhd4Jhncp3OOWM9W78bFif4wzBqMfgI1+fFzbFGSYZq1jUEFH0WJx/UtsyeBmL3siYPfAnqqDCIhvXFudWl49lZi8U9Id0ns/ZBdK/ZojorOWIuvNgP3TZqQc5kuYl9s1Hulef3D72l1RvitAuJXtBkZpOajGi0YdfxEjibkljFftzlA7qZCZeGKijoAdZz+bXKEI02XHOqLW5CjqnWAGvzmHITGmQyCw6OkRDHbwn64nNTOxEevxpU8zgxpG7TC2ej1xprccO/gYaSUglpVJ16xSxDR6YtqZU75DyxywnYJwy4fUmkdCXH0llP2ppht0mGOmwVztvuS81cskWSyeN+YrtnKtgrzm5rlqrkAxte6yyYGl8AS/BhwQW0CxFsllR2qU5IZwH/AYpycGshu3Pf+fTv8cCXqzWfhebXuFvzrvWLI4vsu53u3wQWP3JTXebF/HW/jH2d/ZSj3mkgG9+a19+3+PgbxvzPoO/Pd+1HYvs54rSrpa6Nrc3GH/D9CRPK6ac/yP+NhLHXhyCpj7+D3h8QCsPQ1zNBISRBggT58XcURKAwhVPkHz/exu7Fuu8n4J++eRe2jNn7CeF3rPxqPdu6XGYVMPKgOkGsIvbpUua6XHOCTX8moMHJh8EFm8ZuqszSZSIpa9jUU3ivK7mkcZmVT83RwBfZgaHInzp3B4PTrQ3nvmtnhQSn8vzu+SmfoS1PbEnncsscMRz2segawUvu9+0oduYJiyMKS8j+pZzehzBWpJJ7hK5SJ77bJ437vVzJOlLv/kM5q84kC3q9O79/N7339V0vxwZr46d57+XdV3gm50ZJl5FkgQnXrVfkxzauDdTHjftMGqFJWWwJfXNNRRf9rh82VYevfL0xUZ2Tt+DJvzAK6wAOy9Bzn6FDI/oZNhpsQqGTw/qZwCFn1UETHLqn7cGTxkKHKQ0nedUNUP1ZV1ojlHLefde3h/n6lkD1Yb6HER/tMm+j9zF7DVXZ6uApxm3VkKcPcRtb8VAsU6m17R1w2E5ALG6wpxtLTR7bkWVd9CKdksyWit4iGuW5KOw5S9N6Hg7MKOugbdaDRiQNvV8SGTIkCpqEx4Zt+byest2VV1tqONBKpZJMiTsrrIPMt+fDvGxlPpisCarxdNiJpnL+YtX6uZlbw6Wr5hCsizmN6Mi1eTET2OX89cq3lXob6kOQ7PWJls4BJhEYQqpcRyNJmmB64HEVNu05T10/ZakTdWsxtvpMw4R+AgwcMIa3bEW3yVs23oc7GEO+Y7i925y7Ic70pEuCy7LmnBRujS3I/uwcC+V0tzqx++XGd84WHWAQVMm5wk/06k4Z7U6+ftHK+8SMupdjont2N4RFLlvope4lk+Zxj0r8yicCya2MFuAZfeGprmKsdqKeT1pYxTy/8yo2pvqUF7HLnzLepN6lvxDQ6a5ZQonRKdIVDs3Ejlaj2e1oeOHbMY2WLMFTCHwFrk6aJnEvYUya7PeTqw4pta8MHG5KKk/GJZQxi+PVLjVqghmu2F1COdW2cRt/Mth24zf3QfTVqG+ua9xDcHlEIaZKSnjaSLMoVw2/VOClffY7rt8VYXfDYkShEtrgqm/hgOj92JoReuZnZSirQvDAu5i7tGVUNmPp9nVvo5pvhWBul/6ijyylCzRJekQlEvfOkKqxcwK7NonzeuB3Het1hOreKfuP336OmL5cbfwsZ3+52/hZaL5cbvwsNL/G7cb/P6GBf41LTV/2if7d+0Tob3CfCP0F+0Q+jBKRxnBUnRT2bKXCoHPp0I33AouXuffN22iDV9Qs972lvSkV50pn4htOoRtzla9c7Y3+cOv4VgNuzwQOSLKzeHMUCBO9esWNDRhdg0gvu9BZ01SLaOMFincBIIRGpF8Q9VqSnX/rmr73tXXmUQCR4gV5Zhy8erGKXSTzntvXTujh0WWsq990/K30qO6qPHmEBiVpi+xCp+15Amv5Ku+TzdGdojchcewdyFnggibVvby+7xMN3C0viCoXws3l1OZm0mMyZkUzAg4SrHXYjxCwDXgZi1U8V0/dMZcq2ZiSv3OkFeXSXd+MfcfvwjWqTBTtEQa8mdJkCOFlTTIZRQ/Mm+TbAa+ImqR9jKWhq4qQ/XwaLCuq/PTyHptLPpxVluem6S5QwtS7TemteY2QNKMfXJwpOyHJCuTFRmA9D1c3hNlNCCElfN87l6g/3IdXoFZ0FoO610lVEOqi0Gdw9tyAQA8BEitDuvK0WEK3TeVCjXO3CNzVPdTusMLPPV555jBiqoXfLn5wpjpK0bfKTCxcAxBZoS7hgBDD4GvPtKLy1u+RZp4yB8I8sOZn59YKColctfxp6aIfKSbJQ5q8M6JU17AvAKm+EYC/qsEGzMdkr1D2cgAdWStfjuncTaN1PSMzU5BMund7rc5mrCaF2gb+xq5WHii2MUGtKQuUFz+annt6ipfntx1nDhqpqoBl1xW5VyKUnloUcbT4SPaMFwJ13Auka2TcntKSdR2YSLu0ue+wcgohGl2SBTw6PIF2YIkF3BdQHFbt/sBpbQUPJ+lDv+GWhHIuwVBTNfEk2GvMPB+QwZiEfYegdpYyPng82pCCF3vABcQfsjRJ5ucTruD2em+z7Bxbf+fleMZQCTk3nOwIc4Y6fizW+KKtt6TB4RMNYQL3a/AZADjBJFA3U+MtJ388XvmMvf9yU/M3Ye+x36C9x37J9QPkTjwNxgLqaKt12nz1sLXzAOHtfO/0e86ZT2PLTkGowwUdAvdpEcFpQz706Kncf81rVJBg+xHzM+vrDtziG3AKSMWQD43YU5S4veJc9mbHZIZ6ZHzoPA2Y1IjxJ5ABxWNSKE+UQ7q4bRGj6MvLUt0FuZ8Qxsw5L0tyAUpvWEwB4/OZpoO0k7h+o3FVVROAuSJz+uDutVwa5NMybpZIu/d9A/WVEmoZbSFCB7rcsTCQQQWTQZGHYVcCyygIOGvEwKYBrVDUyVi70dJl+DJlPCNnvNbmC3v4YH/N+BM25bySxHjsKOtWByciEfTU9DS/KfKh3DZhYGZ7ljtBWOzIDshI5LJFfbAPm2yEAjcF11061DHdlb5thAI/DMR/Tq3c8BF5mZ8WP1Wiwso4iaWtmhExvxjs8+ntYus1UmuVAY8bzVK8Ak4qqEQ6k3r/UTnPhpOTu0NdRBiQ9AeuXdHYgbzneMFpSyicC3GjTWsKVfFemI81JaASkArn5gi3hJbv9D1FHECxG/HFtcm9Y8YwnylZAEDeFQJDvD7FdWqYemndPHzhTCRG0zPloIzQLYS5QuU2kL42Nqh4K9Nmc1xOQhrD6iC3ne6hsMeCpewV8amwwwB3PJc+Pb28quM1JS9DHPMCtF83F0f6246WfpV3M5guV4KhRFI9AtIBSIY8gwMiHBi5PPAOjs0UglCogusoH7L+QeuwR2QJ3mzDBZV0o6Eecui3jBtvJ3bYV9BsqxLQrhipWHbHjSx24Yag0hw+nPmOvC85zcHkvcQSBTytpueDwWnO+P5U2SqHg7a7XGr8+WhcVq+QVMfOXAqyh4yUq/kYhRllqYMo0i6edYAgcWIxMTMyJlLVUuLO6Kvo5AiD45AxFvpys/Piqt3TY5EA4FEnXHLK3QOy0g5iFxqyhRLgOTWX1Gjz7IpsMHFqTmvUihaps8LZ84NTpybCJxlVLvnjiFG3ZsyzI7Y+gPR5IGwwKWEILvkCN2ZD1KlGmIfsfrAodFOMPOCwSDm21pIsla+c9AExU/xS0LdfdMzvj0wAVQgX3ABQOGyK2TCGqOTKnK3f4CtzCHEqG2hQciw0kmnhs5NFNSgPPFzZWXwnqvPYBeEb0RKXy9SjWY5OnVArjlQP5wGDoDgy53pbwezqHBknPZjHI80GGKeEGPaVpyRQkXCvdWRTHIGLcbxwQcUm/UDBKkwqCBJagEBJbFzhZH4lEeS6Ywk/uJvNdutVNl6L4UGZk8o31o4x7VWNXp7INrqQaqoH0xWsQkIvZPOlm6Cd0PvurqxXYPUjzTnjqPB9XsNK0bk7pfjwZyqckbk/wSnZLrfLxdERUUM8KlgB1QQ6f9R0mHL8yzLxxgLv94vOjsDAh2u6r07qYgA7oni2kvG6rf1TWwhW8//BuQ78H3T/7b86hv17BOoU5fTWRFU2vZXzW9111Vvedemb/Ja/iHL6JVHvl5/ifxaaLweGn4Xm3x4YxvXXL3X+Cpyuf3ewXm+cl6/7yn9/++mDPL1JZZpm7fu/nkl+KJEeL/esTP72n9P8lTz8/adoPzT94X//z395+8sPVt9eifcN5P8Dq4iAqPBGAAA=";
                                TemplateUtils.applyRawTemplateNBT(stack, "Schematic Loader", "CodeUtilities", templateData);
                                stack.setCustomName(new LiteralText("§b§lFunction §3» Code§bUtilities§d Schematic Loader"));
                                ChatUtil.sendMessage("You received the §dSchematic Loader§b! Place it down in your codespace and open the chest to get functions!",
                                        ChatType.INFO_BLUE);
                                ItemUtil.giveCreativeItem(stack);
                            } else {
                                ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
                            }
                            return 1;
                        })
                );
        return literal;
    }

}
