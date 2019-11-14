package com.sequenceiq.cloudbreak.cloud.azure;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;

import com.sequenceiq.cloudbreak.cloud.azure.validator.AzurePremiumValidatorService;

@RunWith(Parameterized.class)
public class AzurePremiumInstanceTest {

    @InjectMocks
    private final AzurePremiumValidatorService underTest = new AzurePremiumValidatorService();

    private final String instanceType;

    private final boolean premiumInstance;

    public AzurePremiumInstanceTest(String instanceType, boolean premiumInstance) {
        this.instanceType = instanceType;
        this.premiumInstance = premiumInstance;
    }

    @Parameters(name = "{index}: instanceType is premium({0})={1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Standard_A4", false},
                {"Standard_A2_v2", false},
                {"Standard_DS14_v2", true},
                {"Standard_D64s_v3", true},
                {"Standard_A8m_v2", false},
                {"Standard_A2m_v2", false},
                {"Standard_D14", false},
                {"Standard_DS13", true},
                {"Standard_DS12_v2", true},
                {"Standard_D2s_v3", true},
                {"Basic_A0", false},
                {"Standard_E16_v3", false},
                {"Standard_DS15_v2", true},
                {"Standard_E8_v3", false},
                {"Standard_E16s_v3", true},
                {"Standard_DS3", true},
                {"Standard_A10", false},
                {"Standard_D4_v2_Promo", false},
                {"Standard_B1s", false},
                {"Standard_E32_v3", false},
                {"Standard_E8s_v3", true},
                {"Standard_D32s_v3", true},
                {"Standard_DS13_v2_Promo", true},
                {"Standard_NC24", false},
                {"Standard_DS14-8_v2", true},
                {"Standard_DS11_v2_Promo", true},
                {"Standard_D2_v2_Promo", false},
                {"Standard_A11", false},
                {"Basic_A3", false},
                {"Standard_E32-16s_v3", true},
                {"Standard_DS3_v2", true},
                {"Standard_D11", false},
                {"Standard_E4_v3", false},
                {"Standard_E64s_v3", true},
                {"Standard_E64-32s_v3", true},
                {"Standard_B8ms", false},
                {"Standard_A2", false},
                {"Standard_DS14-4_v2", true},
                {"Standard_E32s_v3", true},
                {"Standard_D4_v3", false},
                {"Standard_NV24", false},
                {"Standard_DS1_v2", true},
                {"Standard_DS5_v2", true},
                {"Standard_D4_v2", false},
                {"Standard_D5_v2_Promo", false},
                {"Standard_NC6", false},
                {"Basic_A1", false},
                {"Standard_D12_v2_Promo", false},
                {"Standard_D4s_v3", true},
                {"Standard_D2_v3", false},
                {"Standard_B1ms", false},
                {"Standard_F16", false},
                {"Standard_D1_v2", false},
                {"Standard_A1_v2", false},
                {"Standard_DS12", true},
                {"Standard_F4s", true},
                {"Standard_DS2_v2", true},
                {"Standard_F2", false},
                {"Standard_H16r", false},
                {"Standard_DS14_v2_Promo", true},
                {"Standard_D2", false},
                {"Standard_D14_v2_Promo", false},
                {"Standard_DS4_v2_Promo", true},
                {"Standard_NV6", false},
                {"Standard_E64_v3", false},
                {"Standard_DS14", true},
                {"Standard_A0", false},
                {"Standard_F2s", true},
                {"Standard_B2s", false},
                {"Standard_D32_v3", false},
                {"Standard_H16", false},
                {"Standard_D16_v3", false},
                {"Standard_DS1", true},
                {"Standard_A4m_v2", false},
                {"Standard_D3_v2_Promo", false},
                {"Standard_A6", false},
                {"Standard_D15_v2", false},
                {"Standard_E2s_v3", true},
                {"Standard_A1", false},
                {"Standard_DS11", true},
                {"Standard_H8", false},
                {"Standard_DS12_v2_Promo", true},
                {"Standard_DS3_v2_Promo", true},
                {"Standard_DS4_v2", true},
                {"Standard_D5_v2", false},
                {"Basic_A4", false},
                {"Standard_E64-16s_v3", true},
                {"Standard_D12_v2", false},
                {"Standard_A3", false},
                {"Standard_F4", false},
                {"Standard_A8_v2", false},
                {"Standard_B4ms", false},
                {"Standard_F16s", true},
                {"Standard_A4_v2", false},
                {"Standard_D8s_v3", true},
                {"Standard_D12", false},
                {"Standard_F8", false},
                {"Standard_A7", false},
                {"Standard_DS13-4_v2", true},
                {"Standard_D13", false},
                {"Standard_DS5_v2_Promo", true},
                {"Standard_DS13_v2", true},
                {"Standard_DS2_v2_Promo", true},
                {"Standard_DS4", true},
                {"Standard_B2ms", false},
                {"Standard_F1", false},
                {"Standard_D11_v2_Promo", false},
                {"Standard_NC12", false},
                {"Standard_D2_v2", false},
                {"Standard_D4", false},
                {"Standard_D64_v3", false},
                {"Standard_DS13-2_v2", true},
                {"Standard_DS2", true},
                {"Standard_F8s", true},
                {"Standard_NC24r", false},
                {"Basic_A2", false},
                {"Standard_H16mr", false},
                {"Standard_D14_v2", false},
                {"Standard_D11_v2", false},
                {"Standard_A5", false},
                {"Standard_A9", false},
                {"Standard_A8", false},
                {"Standard_D8_v3", false},
                {"Standard_H16m", false},
                {"Standard_F1s", true},
                {"Standard_H8m", false},
                {"Standard_D1", false},
                {"Standard_D13_v2_Promo", false},
                {"Standard_D13_v2", false},
                {"Standard_E2_v3", false},
                {"Standard_E4s_v3", true},
                {"Standard_D3_v2", false},
                {"Standard_DS11_v2", true},
                {"Standard_D3", false},
                {"Standard_E32-8s_v3", true},
                {"Standard_D16s_v3", true},
                {"Standard_NV12", false},
                {"Standard_GS1", true},
                {"Standard_GS2", true},
                {"Standard_GS3", true},
                {"Standard_GS4", true},
                {"Standard_GS5", true},
                {"Standard_L4s", true},
                {"Standard_L8s", true},
                {"Standard_L16s", true},
                {"Standard_L32s", true},
        });
    }

    @Test
    public void testPremiumInstanceWhichDependsOnThePremiumVariable() {
        Assert.assertEquals(premiumInstance, underTest.validPremiumConfiguration(instanceType));
    }
}
