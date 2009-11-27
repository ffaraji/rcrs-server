java -Xmx1024m -cp ..\lib\uncommons-maths-1.2.jar;..\lib\rescuecore.jar;..\lib\traffic-old.jar;..\lib\resq-fire.jar;..\jars\kernel.jar;..\jars\rescuecore2.jar;..\jars\standard.jar;..\jars\handy.jar;..\jars\human.jar;..\jars\sample.jar;..\jars\misc.jar;..\jars\blockade.jar;..\jars\collapse.jar;..\jars\traffic-old.jar;..\jars\resq-fire.jar;..\jars\ignition.jar kernel.StartKernel -c config --kernel.simulators.auto=collapse.CollapseSimulator,blockade.BlockadeSimulator,misc.MiscSimulator,firesimulator.FireSimulatorWrapper,traffic.TrafficSimulatorWrapper,ignition.IgnitionSimulator --kernel.agents.auto=sample.SampleCivilian*n,sample.SampleFireBrigade*n,sample.SamplePoliceForce*n,sample.SampleAmbulanceTeam*n,sample.SampleCentre*n --kernel.viewers.auto=sample.SampleViewer --kernel.gis.auto=rescuecore2.standard.kernel.StandardWorldModelCreator --kernel.perception.auto=rescuecore2.standard.kernel.StandardPerception --kernel.communication.auto=rescuecore2.standard.kernel.ChannelCommunicationModel --gis.map.dir=..\maps\Kobe --just-run