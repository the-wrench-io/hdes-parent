package io.resys.hdes.compiler.api;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

public interface Flow2 {
  
  FlowState apply(FlowStart input);
  FlowState apply(FlowSuspended state, FlowData fields);
  
  interface FlowData {}
  interface FlowState {}

  interface FlowStart extends FlowState {
    long getStart();
    FlowData getValue();
  }

  interface FlowEnd extends FlowState {
    long getStart();
    Optional<Long> getEnd();
    Optional<Long> getDuration();
    FlowState getParent();
    FlowData getValue();
  }
  
  interface FlowTask extends FlowState {
    long getStart();
    Optional<Long> getEnd();
    Optional<Long> getDuration();
    String getId();
    FlowState getParent();
    Optional<FlowTaskRef> getRef();
  }
  
  interface FlowTaskRef {
    String getId();
  }
  
  interface FlowTaskLoop extends FlowTaskRef {
    List<FlowState> getValues();
  }
  
  interface FlowTaskRefObject extends FlowTaskRef {
    FlowData getIn();
    Optional<FlowData> getOut();
  }
  
  interface FlowTaskRefArray extends FlowTaskRef {
    FlowData getIn();
    List<FlowData> getOut();
  }
  
  interface FlowSuspended {
    FlowState getParent();
  }
  
  interface FlowSerializer {
    Flow2 read(InputStream inputStream);
    void write(Flow2 flow, OutputStream output);
  }
}