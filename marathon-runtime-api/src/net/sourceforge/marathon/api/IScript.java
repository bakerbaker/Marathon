/*******************************************************************************
 *  
 *  Copyright (C) 2010 Jalian Systems Private Ltd.
 *  Copyright (C) 2010 Contributors to Marathon OSS Project
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 * 
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 *  Project website: http://www.marathontesting.com
 *  Help: Marathon help forum @ http://groups.google.com/group/marathon-testing
 * 
 *******************************************************************************/
package net.sourceforge.marathon.api;

import java.util.Properties;

import net.sourceforge.marathon.api.module.Module;

public interface IScript {
    public abstract Module getModuleFuctions();

    public abstract IPlayer getPlayer(IPlaybackListener playbackListener, PlaybackResult result);

    public abstract void runFixtureSetup();

    public abstract void runFixtureTeardown();

    public abstract void exec(String function);

    public abstract IDebugger getDebugger();

    public abstract void attachPlaybackListener(IPlaybackListener listener);

    public abstract Runnable playbackBody(boolean shouldRunFixture, Thread playbackThread);

    public abstract String evaluate(String code);

    public abstract boolean isCustomAssertionsAvailable();

    public abstract void setDataVariables(Properties dataVariables);

}
