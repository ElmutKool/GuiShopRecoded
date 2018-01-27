package ru.blc.guishop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.blc.guishop.gui.GUI;
import ru.blc.guishop.gui.Tab;
import ru.blc.guishop.lang.Phrases;
import ru.blc.guishop.lang.Phrases.Vars;
import ru.blc.guishop.utils.SU;

public class Cmds implements CommandExecutor {
    public Cmds() {
        GuiShopRecoded.instance().getCommand("shop").setExecutor(this);
        GuiShopRecoded.instance().getCommand("shop").setTabCompleter(new Cmds.CmdsTabCompleter());
    }

    public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
        switch(args.length) {
            case 0:
                if(sender.equals(Bukkit.getConsoleSender()) && args.length == 0) {
                    sender.sendMessage("§cOnly for players");
                    return true;
                } else {
                    Player p = (Player)sender;
                    if(!p.hasPermission("shop.use")) {
                        p.sendMessage(SU.genMessage(Phrases.NoPermission.getMessage()));
                        return true;
                    } else if(p.getGameMode() == GameMode.CREATIVE && !p.hasPermission("shop.gamemode")) {
                        p.sendMessage(SU.genMessage(Phrases.CreativePermission.getMessage()));
                        return true;
                    } else if(!p.hasPermission("shop.use")) {
                        p.sendMessage(SU.genMessage(Phrases.NoPermission.getMessage()));
                        return true;
                    } else {
                        GUI.openMain(p);
                        p.sendMessage(SU.genMessage(Phrases.InShop.getMessage()));
                        return true;
                    }
                }
            case 1:
                if(args[0].equalsIgnoreCase("help") || !args[0].equalsIgnoreCase("reload") && !args[0].equalsIgnoreCase("setsale")) {
                    break;
                }

                if(args[0].equalsIgnoreCase("setsale")) {
                    sender.sendMessage("§a/shop setsale <tab_name> <sale in percentages> [sale_Reason] §6use \"_\" instead \" \"");
                    return true;
                }

                if(!sender.hasPermission("shop.reload") && args.length == 1 && args[0].toLowerCase().equalsIgnoreCase("reload")) {
                    sender.sendMessage(SU.genMessage(Phrases.NoPermission.getMessage()));
                    return true;
                }

                GuiShopRecoded.instance().reloadPlugin();
                sender.sendMessage(SU.genMessage(Phrases.Reloaded.getMessage()));
                return true;
            case 2:
            default:
                if(args[0].equalsIgnoreCase("setsale")) {
                    sender.sendMessage("§a/shop setsale <tab_name> <sale in percentages> [sale_Reason] §6use \"_\" instead \" \"");
                    return true;
                }
                break;
            case 3:
                if(args[0].equalsIgnoreCase("setsale")) {
                    double sale = 0.0D;
                    if(!SU.isNumeric(args[2])) {
                        sender.sendMessage(SU.genMessage(Phrases.NotNomber.builder().replaceVar(Vars.STRING, args[2]).buildMessage()));
                        return true;
                    }

                    sale = Double.parseDouble(args[2]);
                    if(!args[1].equalsIgnoreCase("all")) {
                        Tab tab = Tab.getTab(args[1].replace("_", " "));
                        if(tab == null) {
                            sender.sendMessage(SU.genMessage(Phrases.TabNotFound.builder().replaceVar(Vars.TAB, args[1]).buildMessage()));
                            printTabs(sender);
                            return true;
                        }

                        tab.setSale(sale, "");
                        sender.sendMessage(SU.genMessage(Phrases.SaleInstalled.builder().replaceVar(Vars.TAB, args[1]).replaceVar(Vars.DISCOUNT, args[2]).replaceVar(Vars.REASON, "").buildMessage()));
                        return true;
                    }

                    Iterator var14 = Tab.getTabsSections().keySet().iterator();

                    while(var14.hasNext()) {
                        String s = (String)var14.next();
                        Tab.getTab(s).setSale(sale, "");
                    }

                    sender.sendMessage(SU.genMessage(Phrases.SaleInstalled.builder().replaceVar(Vars.TAB, args[1]).replaceVar(Vars.DISCOUNT, args[2]).replaceVar(Vars.REASON, "").buildMessage()));
                    return true;
                }
                break;
            case 4:
                if(args[0].equalsIgnoreCase("setprice")) {
                    sender.sendMessage("Called setprice!");

                    Tab tab = Tab.getTab(args[1].replace("_", " "));



                    return true;
                }

                if(args[0].equalsIgnoreCase("setsale")) {
                    double sale1 = 0.0D;
                    if(!SU.isNumeric(args[2])) {
                        sender.sendMessage(SU.genMessage(Phrases.NotNomber.builder().replaceVar(Vars.STRING, args[2]).buildMessage()));
                        return true;
                    }

                    sale1 = Double.parseDouble(args[2]);
                    if(!args[1].equalsIgnoreCase("all")) {
                        Tab tab1 = Tab.getTab(args[1].replace("_", " "));
                        if(tab1 == null) {
                            sender.sendMessage(SU.genMessage(Phrases.TabNotFound.builder().replaceVar(Vars.TAB, args[1]).buildMessage()));
                            printTabs(sender);
                            return true;
                        }

                        tab1.setSale(sale1, args[3].replace("_", " "));
                        sender.sendMessage(SU.genMessage(Phrases.SaleInstalled.builder().replaceVar(Vars.TAB, args[1]).replaceVar(Vars.DISCOUNT, args[2]).replaceVar(Vars.REASON, args[3].replace("_", " ")).buildMessage()));
                        return true;
                    }

                    Iterator var12 = Tab.getTabsSections().keySet().iterator();

                    while(var12.hasNext()) {
                        String s = (String)var12.next();
                        Tab.getTab(s).setSale(sale1, args[3].replace("_", " "));
                    }

                    sender.sendMessage(SU.genMessage(Phrases.SaleInstalled.builder().replaceVar(Vars.TAB, args[1]).replaceVar(Vars.DISCOUNT, args[2]).replaceVar(Vars.REASON, args[3].replace("_", " ")).buildMessage()));
                    return true;
                }
        }

        printUsage(sender);
        return true;
    }

    public static void printUsage(CommandSender sender) {
        sender.sendMessage("§a/shop");
        sender.sendMessage("§a/shop reload");
        sender.sendMessage("§a/shop setsale <tab_name> <sale in percentages> [sale_Reason] §6use \"_\" instead \" \"");
    }

    public static void printTabs(CommandSender sender) {
        String tabs = "";

        String s;
        for(Iterator var3 = Tab.getTabsSections().keySet().iterator(); var3.hasNext(); tabs = tabs + s.replace(" ", "_") + ", ") {
            s = (String)var3.next();
        }

        tabs = tabs.length() >= 2?tabs.substring(0, tabs.length() - 2):tabs;
        sender.sendMessage(SU.genMessage(Phrases.AvalibleTabs.builder().replaceVar(Vars.TABS, tabs).buildMessage()));
    }

    public class CmdsTabCompleter implements TabCompleter {
        public CmdsTabCompleter() {
        }

        public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args) {
            HashSet<String> completers = new HashSet();
            switch(args.length) {
                case 1:
                    if(sender.hasPermission("shop.reload") && "reload".startsWith(args[0].toLowerCase())) {
                        completers.add("reload");
                    }

                    if(sender.hasPermission("shop.setsale") && "setsale".startsWith(args[0].toLowerCase())) {
                        completers.add("setsale");
                    }
                    break;
                case 2:
                    if(args[0].equalsIgnoreCase("setsale") && sender.hasPermission("shop.setsale")) {
                        Iterator var7 = Tab.getTabsSections().keySet().iterator();

                        while(var7.hasNext()) {
                            String s = (String)var7.next();
                            if(s.replace(" ", "_").toLowerCase().startsWith(args[1].toLowerCase())) {
                                completers.add(s.replace(" ", "_"));
                            }

                            if("all".startsWith(args[1].toLowerCase())) {
                                completers.add("all");
                            }
                        }
                    }
            }

            return new ArrayList(completers);
        }
    }
}