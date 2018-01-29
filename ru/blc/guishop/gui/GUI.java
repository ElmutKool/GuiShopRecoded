package ru.blc.guishop.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import ru.blc.guishop.GuiShopRecoded;
import ru.blc.guishop.gui.ShopInventory.OperationType;
import ru.blc.guishop.lang.Phrases;
import ru.blc.guishop.utils.ItemUtil;
import ru.blc.guishop.utils.SU;

public class GUI {
    private static HashMap<Inventory, ShopInventory> inventories = new HashMap();
    private static Inventory main;
    private static List<Inventory> sellTabsPages = new ArrayList();
    private static List<Inventory> buyTabsPages = new ArrayList();
    private static List<Tab> tabs = new ArrayList();
    private static List<Tab> vipTabs = new ArrayList();

    public static List<Tab> getTabs() {
        return tabs;
    }

    public GUI() {
    }

    public static void createShop() {
        generateMain();
        generateTabs();
        generateVipTabs();
        generateTabsList(OperationType.BUY);
        generateTabsList(OperationType.SELL);
    }

    public static void endShop() {
        closeAllInventoryes();
        inventories.clear();
        main.clear();
        sellTabsPages.clear();
        buyTabsPages.clear();
        tabs.clear();
        vipTabs.clear();
    }

    public static void closeAllInventoryes() {
        Iterator var1 = Bukkit.getOnlinePlayers().iterator();

        while(var1.hasNext()) {
            Player p = (Player)var1.next();
            if(p.getOpenInventory() != null && p.getOpenInventory().getTopInventory() != null && getInventories().containsKey(p.getOpenInventory().getTopInventory())) {
                p.closeInventory();
            }
        }

    }

    public static void openMain(Player p) {
        if(!p.hasPermission("shop.use")) {
            p.sendMessage(SU.genMessage(Phrases.NoPermission.getMessage()));
        } else {
            p.openInventory(main);
        }
    }

    public static void openTab(Player p, Tab tab, int page, OperationType type) {
        Bukkit.getConsoleSender().sendMessage(String.valueOf(page));
        if(!p.hasPermission(tab.getPermission()) && !tab.getPermission().isEmpty()) {
            p.sendMessage(SU.genMessage(Phrases.NoTabPermission.getMessage()));
        } else {
            p.openInventory(tab.getPage(page, type));
        }
    }

    public static void openTabFromList(Player p, OperationType type, int listPage, int slot) {
        int realNumber = (listPage - 1) * 42 + slot - 2 * (int)Math.ceil((double)slot / 9.0D);
        List<String> keysAll = new ArrayList(GuiShopRecoded.getTabs().getKeys(false));

        for(int i = 0; i < keysAll.size(); ++i) {
            if(!GuiShopRecoded.getTabs().getBoolean((String)keysAll.get(i) + ".Enable" + SU.firstUpperCase(type.name()))) {
                ++realNumber;
            }

            if(i == realNumber) {
                if(!p.hasPermission(((Tab)tabs.get(i)).getPermission()) && !((Tab)tabs.get(i)).getPermission().isEmpty()) {
                    p.sendMessage(SU.genMessage(Phrases.NoTabPermission.getMessage()));
                } else {
                    p.openInventory(((Tab)tabs.get(i)).getPage(1, type));
                }
                break;
            }
        }

    }

    public static void openVip(Player p, int index, int page) {
        if(!p.hasPermission(((Tab)vipTabs.get(index - 1)).getPermission()) && !((Tab)vipTabs.get(index - 1)).getPermission().isEmpty()) {
            p.sendMessage(SU.genMessage(GuiShopRecoded.getMain().getString("vipmenu" + String.valueOf(index) + ".PermissionMessage")));
        } else {
            p.openInventory(((Tab)vipTabs.get(index - 1)).getPage(page, OperationType.BUY));
        }
    }

    public static void openTabsList(Player p, int page, OperationType oType) {
        if(!p.hasPermission("shop." + oType.name().toLowerCase().toLowerCase())) {
            p.sendMessage(SU.genMessage(Phrases.NoPermission.getMessage()));
        } else {
            p.openInventory(getTabsListPage(page, oType));
        }
    }

    public static Inventory getTabsListPage(int page, OperationType oType) {
        page = page - 1 < 0?1:page;
        return oType == OperationType.BUY?(Inventory)buyTabsPages.get(page - 1 >= buyTabsPages.size()?buyTabsPages.size() - 1:page - 1):(oType == OperationType.SELL?(Inventory)sellTabsPages.get(page - 1 >= sellTabsPages.size()?sellTabsPages.size() - 1:page - 1):null);
    }

    public static void generateTabsList(OperationType oType) {
        List<String> keysAll = new ArrayList(GuiShopRecoded.getTabs().getKeys(false));
        List<String> keysEnabled = new ArrayList();
        Iterator var4 = keysAll.iterator();

        while(var4.hasNext()) {
            String s = (String)var4.next();
            if(GuiShopRecoded.getTabs().getBoolean(s + ".Enable" + SU.firstUpperCase(oType.name()))) {
                keysEnabled.add(s);
            }
        }

        boolean havepages = keysEnabled.size() > 42;

        for(int j = 0; (double)j < Math.ceil((double)keysEnabled.size() / 42.0D); ++j) {
            boolean lastpage = (double)(j + 1) >= Math.ceil((double)keysEnabled.size() / 42.0D);
            int strings = lastpage?((int)Math.ceil((double)(keysEnabled.size() - j * 42) / 7.0D) < 4 && havepages?4:(int)Math.ceil((double)(keysEnabled.size() - j * 42) / 7.0D)):6;
            ShopInventory sinv = new ShopInventory((InventoryHolder)null, strings * 9, SU.genName(GuiShopRecoded.getMain().getString(oType.name().toLowerCase() + "item.Name")), oType);
            Inventory inv = sinv.getInventory();
            inv.setItem(0, ItemUtil.load(GuiShopRecoded.getMain().getConfigurationSection("Main")));
            int i;
            int slot;
            if(havepages) {
                ItemStack item = ItemUtil.load(GuiShopRecoded.getMain().getConfigurationSection(oType.name().toLowerCase() + "item"));
                i = (double)(j + 2) > Math.ceil((double)keysEnabled.size() / 42.0D)?(int)Math.ceil((double)keysEnabled.size() / 42.0D):j + 2;
                slot = j == 0?1:j;
                item.setAmount(slot);
                inv.setItem(18, item);
                item.setAmount(i);
                inv.setItem(27, item);
            }

            List<ItemStack> items = new ArrayList();
            Iterator var17 = keysEnabled.iterator();

            while(var17.hasNext()) {
                String s = (String)var17.next();
                items.add(ItemUtil.load(GuiShopRecoded.getTabs().getConfigurationSection(s)));
            }

            for(i = j * 42; i < (j + 1) * 42; ++i) {
                slot = i - j * 42 + 2 * ((int)Math.ceil((double)((i - j * 42) / 7)) + 1);
                ItemStack item = (ItemStack)items.get(i);
                inv.setItem(slot, item);
                if(i + 1 >= items.size()) {
                    break;
                }
            }

            sinv.setInventory(inv);
            if(oType == OperationType.SELL) {
                sellTabsPages.add(inv);
            }

            if(oType == OperationType.BUY) {
                buyTabsPages.add(inv);
            }

            registerInventory(inv, sinv);
        }

    }

    public static void generateTabs() {
        Iterator var1 = GuiShopRecoded.getTabs().getKeys(false).iterator();

        while(var1.hasNext()) {
            String s = (String)var1.next();
            tabs.add(Tab.loadTab(s));
        }

    }

    public static void generateVipTabs() {
        for(int i = 1; i <= 3; ++i) {
            vipTabs.add(Tab.loadVipTab(i));
        }

    }

    public static void generateMain() {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 9, SU.genName(GuiShopRecoded.getMain().getString("Main.Name")));
        inv.setItem(0, ItemUtil.load(GuiShopRecoded.getMain().getConfigurationSection("buyitem")));
        inv.setItem(1, ItemUtil.load(GuiShopRecoded.getMain().getConfigurationSection("sellitem")));
        if(GuiShopRecoded.getMain().getBoolean("vipmenu1.Show")) {
            inv.setItem(3, ItemUtil.load(GuiShopRecoded.getMain().getConfigurationSection("vipmenu1")));
        }

        if(GuiShopRecoded.getMain().getBoolean("vipmenu2.Show")) {
            inv.setItem(4, ItemUtil.load(GuiShopRecoded.getMain().getConfigurationSection("vipmenu2")));
        }

        if(GuiShopRecoded.getMain().getBoolean("vipmenu3.Show")) {
            inv.setItem(5, ItemUtil.load(GuiShopRecoded.getMain().getConfigurationSection("vipmenu3")));
        }

        inv.setItem(7, ItemUtil.load(GuiShopRecoded.getMain().getConfigurationSection("help1")));
        inv.setItem(8, ItemUtil.load(GuiShopRecoded.getMain().getConfigurationSection("help2")));
        main = inv;
        registerInventory(inv, new ShopInventory(main, (OperationType)null));
    }

    public static void registerInventory(Inventory inventory, ShopInventory sinventory) {
        inventories.put(inventory, sinventory);
    }

    public static void unregisterInventory(Inventory inventory) {
        inventories.remove(inventory);
    }

    public static HashMap<Inventory, ShopInventory> getInventories() {
        return inventories;
    }

    public static Inventory getMainInventory() {
        return main;
    }

    public static List<Inventory> getBuyTabsPages() {
        return buyTabsPages;
    }

    public static List<Inventory> getSellTabsPages() {
        return sellTabsPages;
    }
}
