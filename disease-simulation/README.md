# Project 4: Disease Simulation
#### Authors: Alex Hartel, Justin Nelson, Group 11

-----------

## Description
This project is meant to model a disease spreading through a population. The agents in the simulations represent any living being.
It is a simple simulation where contagious agents expose all agents within a given distance
without worrying about the actual details of the disease.
Stages include:
- Vulerable - If exposed to a sick agent, will become sick after some delay. Color: Aqua
- Sick - Can spread the disease, will either recover or die after some delay. Color: Yellow
- Immune - Does not get sick after exposure to sick agent. Color: White
- Dead - Doesn't do anything. Color: Red

>How to 
1. Configure settings (2 options)
   1. Import a text file using 'Select File...' button
   2. Manually set values using sliders
   - Note: The settings default to random if 'Grid' or 'Random Grid' is checked. Each of the sliders is preset to defaults outlined in project descriptions. 
2. Click the SET VALUES button
   - Once SET VALUES is pressed configuration parameters shows in command line (for both file and manual input).
3. Click RUN (simulation will automatically start below, STOP and RESET will appear to the right)
4. To end the simulation click STOP. **The simulation will not stop by itself**
5. Once stopped, user can either RESET the program or press EXIT to kill the program

>Setting parameters
- Dimensions: Creates a pane with the desired settings left slider is for X-axis size, right slider is for Y-axis selecting. Sizing is in pixels. This also holds true for Grid and Random Grid.
- Grid: Selecting this will give a grid full of agents using Exposure Distance as a separator. The grid will contain X-axis*Y-axis agents. Each created agent will still be randomly placed inside grid. Initially selected agents are randomly selected.
- Random Grid: Selecting this will provide a grid containing a preset amount of agents, all randomly placed throughout grid.
- Incubation,Sickness Time: unit in seconds
- Recovery/Immune both given in percentages, later calculated when assembling agents.

>Options chosen: 
- Display a history of the simulation in seperate window
- Display a plot with x and y axis
- Rerun simulation
- Initial immunity

>Known Issues
- Sometimes the simulation graph will not start at the correct time, example it will wait until 1000 to start, which is a restart bug that is still trying to get worked out
- Sometimes the event log would skip events, seems to be fixed with a while loop check to interrupt threads
- Colors for the legend had to be forced into change and sometimes will be a bit buggy and revert to default colors for a short duration of time
- The initially sick node is typically the first to die. Did not have enough time to further randomize

>Future Development
- Allow movement for Agents
- Fix plotting bugs, like trying something other than a stacked bar chart
- Aquire and implement better thread safety protocols

>Algorithm Description
- Main assembles GUI and waits for user to select SET VALUES. Once values are selected they are packaged in a Configuration object that is sent to Simulator class.
- Using the specified configurations the constructor assembles each agent, specifying initially sick, immune and recovery amount.
- Once assembled the Agent objects (made Runnable) are placed into a Concurrent Hashmap.
- The nodes are randomly placed in the preselected format. 
- Simulation then goes to each node and calculates the distance (in pixels) using the distance formula. The result of this calculation will provide the neighbors. Neighbors are saved within the Agent Object itself.
- Once user selects RUN, each thread will be started, the initially sick nodes will have a sick message in their Blocking Queue. As the agent gets sick it will message its neighboring nodes, which infects them. As the nodes change thier status, the graph will record
the total amount in that status and display on the left half of the GUI. Each node passes a message to a master blocking queue which is presented to the user.
- This process continues until the user clicks STOP or RESET. 
- The GUI will erase and await user input.

>Participation
- Alex Hartel - Half of GUI, Logs, File read in, Configurations, Half of Multithreading
- Justin Nelson - Half of GUI, Agent creation, Agent position, Half of Multithreading