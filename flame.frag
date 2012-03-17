//
// Fragment shader for rendering a "confetti cannon"
// via a partcle system
//
// Author: Randi Rost
//
// Copyright (c) 2003-2004: 3Dlabs, Inc.
//
// See 3Dlabs-License.txt for license information
//

varying vec4 Color;

void main (void)
{

	vec4 color = Color;

	color.r = color.r/2;

//	color.a = Color.b;
    gl_FragColor = color;

}