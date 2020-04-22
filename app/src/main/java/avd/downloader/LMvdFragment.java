

package avd.downloader;

import android.app.Fragment;

public class LMvdFragment extends Fragment {
    public LMvdActivity getLMvdActivity() {
        return (LMvdActivity) getActivity();
    }

    public LMvdApp getLMvdApp() {
        return (LMvdApp) getActivity().getApplication();
    }
}
