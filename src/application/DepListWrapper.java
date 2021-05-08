package application;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlElement;

@XmlRootElement(name="Dep")
public class DepListWrapper 
{
	private List<Dep> dep;
	@XmlElement(name="dep")
	public List<Dep> getDep()
	{
		return dep;
	}
public void setDep(List<Dep>dep)
{
	this.dep=dep;
	
}

	
}
