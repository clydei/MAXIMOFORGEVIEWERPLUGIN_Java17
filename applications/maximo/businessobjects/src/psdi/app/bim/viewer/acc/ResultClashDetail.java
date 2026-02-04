/**
 * Copyright IBM Corporation 2009-2026
 *
 * Licensed under the Eclipse Public License - v 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @Author ACC Plugin Development Team
 * @Since Java 17
 **/
package psdi.app.bim.viewer.acc;

import psdi.app.bim.viewer.dataapi.Result;
import psdi.app.bim.viewer.dataapi.acc.ACCClash;

/**
 * Result class for ACC clash detail operations.
 */
public class ResultClashDetail extends Result {
    
    private ACCClash clash;
    
    public ResultClashDetail() {
        super();
    }
    
    public ACCClash getClash() {
        return clash;
    }
    
    public void setClash(ACCClash clash) {
        this.clash = clash;
    }
}

// Made with Bob
