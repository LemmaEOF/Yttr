/*
 * IBXM2
 * Copyright (c) 2019, Martin Cameron
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the
 * following conditions are met:
 * 
 *  * Redistributions of source code must retain the above
 *    copyright notice, this list of conditions and the
 *    following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 * 
 *  * Neither the name of the organization nor the names of
 *    its contributors may be used to endorse or promote
 *    products derived from this software without specific
 *    prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.unascribed.yttr.repackage.ibxm2;

public class Envelope {
	public boolean enabled = false, sustain = false, looped = false;
	public int sustainTick = 0, loopStartTick = 0, loopEndTick = 0;
	public int numPoints = 1;
	public int[] pointsTick = new int[ 1 ];
	public int[] pointsAmpl = new int[ 1 ];
	
	public int nextTick( int tick, boolean keyOn ) {
		tick++;
		if( looped && tick >= loopEndTick ) tick = loopStartTick;
		if( sustain && keyOn && tick >= sustainTick ) tick = sustainTick;
		return tick;
	}
	
	public int calculateAmpl( int tick ) {
		int ampl = pointsAmpl[ numPoints - 1 ];
		if( tick < pointsTick[ numPoints - 1 ] ) {
			int point = 0;
			for( int idx = 1; idx < numPoints; idx++ )
				if( pointsTick[ idx ] <= tick ) point = idx;
			int dt = pointsTick[ point + 1 ] - pointsTick[ point ];
			int da = pointsAmpl[ point + 1 ] - pointsAmpl[ point ];
			ampl = pointsAmpl[ point ];
			ampl += ( ( da << 24 ) / dt ) * ( tick - pointsTick[ point ] ) >> 24;
		}
		return ampl;
	}
	
	public void toStringBuffer( StringBuffer out, String prefix ) {
		if( sustain ) {
			out.append( prefix + "Sustain Tick: " + sustainTick + '\n' );
		}
		if( looped ) {
			out.append( prefix + "Loop Start Tick: " + loopStartTick + '\n' );
			out.append( prefix + "Loop End Tick: " + loopEndTick + '\n' );
		}
		out.append( prefix + "Points:" );
		for( int point = 0; point < numPoints; point++ ) {
			if( point % 3 == 0 ) {
				out.append( '\n' + prefix + prefix );
			}
			out.append( "(" + pointsTick[ point ] + ", " + pointsAmpl[ point ] + "), " );
		}
		out.append( '\n' );
	}
}
