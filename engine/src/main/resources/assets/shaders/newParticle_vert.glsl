/*
 * Copyright 2015 Benjamin Glatzel <benjamin.glatzel@me.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projMatrix;
uniform mat4 viewProjMatrix;

mat4 undoRotation(in mat4 matrix) {
	mat4 noRotation = mat4(matrix);

	for(int row = 0; row < 3; row++) {
		for(int column = 0; column < 3; column++) {
			noRotation[row][column] = (row == column) ? 1 : 0;
		}
	}

	return noRotation;
}

void main()
{
	gl_Position = ftransform();
    gl_FrontColor = gl_Color;
}
