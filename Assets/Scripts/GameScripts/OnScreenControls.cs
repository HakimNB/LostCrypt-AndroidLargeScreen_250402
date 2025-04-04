using UnityEngine;
using UnityEngine.InputSystem;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public class OnScreenControls : MonoBehaviour
{
    public Canvas controlsCanvas;

    void Awake()
    {
        bool isMobile = SystemInfo.deviceType == DeviceType.Handheld;

#if SIMULATE_MOBILE
        isMobile = true;
#endif

        if (controlsCanvas != null)
            controlsCanvas.gameObject.SetActive(isMobile);

        if (isMobile)
            Application.targetFrameRate = 60;
    
    }

    public void BackToMainmenuScene()
    {
        SceneManager.LoadSceneAsync("Mainmenu");
    }
}
