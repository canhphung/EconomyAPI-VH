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

public class TakeMoneyCommand extends Command {
    private EconomyAPI plugin;

    public TakeMoneyCommand(EconomyAPI plugin) {
        super("takemoney", "Lấy tiền của một người chơi nào đó", "/takemoney <người chơi> <số lượng>", new String[]{"withdraw"});

        this.plugin = plugin;

        // command parameters
        commandParameters.clear();
        commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("amount", CommandParamType.FLOAT, false)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.plugin.isEnabled()) return false;
        if (!sender.hasPermission("economyapi.command.takemoney")) {
            sender.sendMessage(TextFormat.RED + "Bạn không có quyền để sử dụng lệnh này.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage(TextFormat.RED + "Usage: " + this.getUsage());
            return true;
        }
        String player = args[0];

        Player p = this.plugin.getServer().getPlayer(player);
        if (p != null) {
            player = p.getName();
        }
        try {
            double amount = Double.parseDouble(args[1]);
            if (amount < 0) {
                sender.sendMessage(this.plugin.getMessage("takemoney-invalid-number", sender));
                return true;
            }

            int result = this.plugin.reduceMoney(player, amount);
            switch (result) {
                case EconomyAPI.RET_INVALID:
                    sender.sendMessage(this.plugin.getMessage("takemoney-player-lack-of-money", new String[]{player,
                            EconomyAPI.MONEY_FORMAT.format(amount), EconomyAPI.MONEY_FORMAT.format(this.plugin.myMoney(player))}, sender));
                    return true;
                case EconomyAPI.RET_NO_ACCOUNT:
                    sender.sendMessage(this.plugin.getMessage("player-never-connected", new String[]{player}, sender));
                    return true;
                case EconomyAPI.RET_CANCELLED:
                    sender.sendMessage(this.plugin.getMessage("takemoney-failed", new String[]{player}, sender));
                    return true;
                case EconomyAPI.RET_SUCCESS:
                    sender.sendMessage(this.plugin.getMessage("takemoney-took-money", new String[]{player, EconomyAPI.MONEY_FORMAT.format(amount)}, sender));
                    if (p != null) {
                        p.sendMessage(this.plugin.getMessage("takemoney-money-taken", new String[]{EconomyAPI.MONEY_FORMAT.format(amount)}, sender));
                    }
                    return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(this.plugin.getMessage("takemoney-must-be-number", sender));
        }
        return true;
    }

}
