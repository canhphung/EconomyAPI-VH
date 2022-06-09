package me.onebone.economyapi.command;

/*
 * EconomyAPI: Core of economy system for Nukkit
 * Copyright (C) 2016  onebone <jyc00410@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import me.onebone.economyapi.EconomyAPI;

public class MyMoneyCommand extends Command {
    private EconomyAPI plugin;

    public MyMoneyCommand(EconomyAPI plugin) {
        super("mymoney", "Xem số tiền của bạn", "/money", new String[]{"money", "bal", "seemoney", "balance"});

        this.plugin = plugin;

        commandParameters.clear();
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.plugin.isEnabled()) return false;
        if (!sender.hasPermission("economyapi.command.mymoney")) {
            sender.sendMessage(TextFormat.RED + "Bạn không có quyền để sử dụng lệnh này.");
            return false;
        }

        String target;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(TextFormat.RED + "Vui lòng sử dụng lệnh này trong game.");
                return true;
            }
            target = sender.getName();
        } else {
            target = args[0];
        }

        double money = this.plugin.myMoney(target);
        if (money == -1) {
            sender.sendMessage(this.plugin.getMessage("player-never-connected", new String[]{args[0]}, sender));
            return true;
        }

        String moneyString = EconomyAPI.MONEY_FORMAT.format(money);
        if (sender.getName().equals(target)) {
            sender.sendMessage(this.plugin.getMessage("mymoney-mymoney", new String[]{moneyString}, target));
        } else {
            sender.sendMessage(this.plugin.getMessage("seemoney-seemoney", new String[]{target, moneyString}, sender));
        }
        return true;
    }
}
