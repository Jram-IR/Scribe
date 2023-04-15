package com.jayaram.scribe;

public interface ItemTouchAdapter {

    public void OnSwiped(int position);

    public void OnMove(int fromPos , int toPos);

    public void OnMoveComplete();

        public void isSwiped(boolean swipe);

}
